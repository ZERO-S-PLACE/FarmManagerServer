package org.zeros.farm_manager_server.Services.Default.Crop;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.InterCropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.MainCropDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.InterCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Data.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Data.Subside;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.Exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Data.PlantManager;
import org.zeros.farm_manager_server.Services.Interface.Data.SubsideManager;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class CropManagerDefault implements CropManager {
    private final EntityManager entityManager;
    private final CropRepository cropRepository;
    private final SubsideManager subsideManager;
    private final FieldPartManager fieldPartManager;
    private final PlantManager plantManager;
    private final FieldPartRepository fieldPartRepository;


    @Override
    @Transactional(readOnly = true)
    public CropDTO getCropById(UUID cropId) {
        Crop crop= cropRepository.findById(cropId).orElseThrow(()->
                new IllegalArgumentExceptionCustom(Crop.class,IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
        return DefaultMappers.cropMapper.entityToDto(crop);
    }

    @Override
    @Transactional
    public MainCropDTO createNewMainCrop(UUID fieldPartId, Set<UUID> cultivatedPlantsIds) {
        FieldPart fieldPart = fieldPartManager.getFieldPartIfExists(fieldPartId);
        Set<Plant> cultivatedPlants = getPlantsIfExist(cultivatedPlantsIds);
        Crop crop = MainCrop.builder().cultivatedPlants(cultivatedPlants).fieldPart(fieldPart).build();
        Crop cropSaved = cropRepository.saveAndFlush(crop);
        fieldPart.getCrops().add(cropSaved);
        flushChanges();
        return (MainCropDTO)getCropById(cropSaved.getId());
    }



    @Override
    @Transactional
    public InterCropDTO createNewInterCrop(UUID fieldPartId, Set<UUID> cultivatedPlantsIds) {
        FieldPart fieldPart = fieldPartManager.getFieldPartIfExists(fieldPartId);
        Set<Plant> cultivatedPlants = getPlantsIfExist(cultivatedPlantsIds);
        Crop crop = InterCrop.builder().cultivatedPlants(cultivatedPlants).fieldPart(fieldPart).build();
        Crop cropSaved = cropRepository.saveAndFlush(crop);
        fieldPart.getCrops().add(cropSaved);
        flushChanges();
        return (InterCropDTO) getCropById(cropSaved.getId());
    }

    @Override
    @Transactional
    public void deleteCropAndItsData(UUID cropId) {
        Crop crop = cropRepository.findById(cropId).orElse(MainCrop.NONE);
        if (crop == MainCrop.NONE) {return;}
        cropRepository.delete(crop);
        flushChanges();
    }

    @Override
    @Transactional
    public void updateCultivatedPlants(UUID cropId, Set<UUID> cultivatedPlantsIds) {
        Crop crop = getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        Set<Plant> cultivatedPlants = getPlantsIfExist(cultivatedPlantsIds);
        crop.setCultivatedPlants(cultivatedPlants);
        flushChanges();
    }

    private Set<Plant> getPlantsIfExist(Set<UUID> cultivatedPlantsIds) {
        return cultivatedPlantsIds.stream().map(plantManager::getPlantIfExists).collect(Collectors.toSet());
    }


    private void checkOperationModificationAccess(Crop crop) {
        if (crop.getWorkFinished()) {
            throw new IllegalAccessErrorCustom(Crop.class, IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
        }
    }

    @Override
    @Transactional
    public void setDateDestroyed(UUID interCropId, LocalDate dateDestroyed) {
        Crop crop = getCropIfExists(interCropId);
        if (crop instanceof InterCrop) {
            ((InterCrop) crop).setDateDestroyed(dateDestroyed);
            flushChanges();
        }
        throw new IllegalArgumentExceptionCustom(Crop.class, IllegalArgumentExceptionCause.TYPE_MISMATCH);
    }

    @Override
    @Transactional
    public void setWorkFinished(UUID cropId) {
        Crop crop = getCropIfExists(cropId);
        removePlannedOperations(crop);
        crop.setWorkFinished(true);
        flushChanges();
        getCropIfExists(crop.getId());
    }

    private  void removePlannedOperations(Crop crop) {
        crop.getSeeding().removeIf(AgriculturalOperation::getIsPlannedOperation);
        crop.getCultivations().removeIf(AgriculturalOperation::getIsPlannedOperation);
        crop.getFertilizerApplications().removeIf(AgriculturalOperation::getIsPlannedOperation);
        crop.getSprayApplications().removeIf(AgriculturalOperation::getIsPlannedOperation);
        if (crop instanceof MainCrop) {
            ((MainCrop) crop).getHarvest().removeIf(AgriculturalOperation::getIsPlannedOperation);
        }
    }

    @Override
    @Transactional
    public void setFullySold(UUID mainCropId) {
        Crop crop = getCropIfExists(mainCropId);
        if (crop instanceof MainCrop) {
            ((MainCrop) crop).setIsFullySold(true);
            flushChanges();
            return;
        }
        throw new IllegalArgumentExceptionCustom(Crop.class,
                IllegalArgumentExceptionCause.TYPE_MISMATCH);
    }


    @Override
    @Transactional
    public void addSubside(UUID cropId, UUID subsideId) {
        Crop crop = getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        Subside subside = subsideManager.getSubsideIfExists(subsideId);
        if (crop.getSubsides().contains(subside)) {
            return;
        }
        crop.getSubsides().add(subside);
        flushChanges();
    }

    @Override
    @Transactional
    public void removeSubside(UUID cropId, UUID subsideId) {
        Crop crop = getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        Subside subside = subsideManager.getSubsideIfExists(subsideId);
        crop.getSubsides().remove(subside);
        flushChanges();
    }

    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }

    @Override
    @Transactional
    public Crop getCropIfExists(UUID cropId) {
        if(cropId==null){
            throw new IllegalArgumentExceptionCustom(Crop.class,Set.of("id"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        return cropRepository.findById(cropId).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(Crop.class,
                        IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
    }
}
