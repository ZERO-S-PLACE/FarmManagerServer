package org.zeros.farm_manager_server.services.default_impl.crop;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.domain.dto.crop.CropDTO;
import org.zeros.farm_manager_server.domain.dto.crop.InterCropDTO;
import org.zeros.farm_manager_server.domain.dto.crop.MainCropDTO;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;
import org.zeros.farm_manager_server.domain.entities.crop.InterCrop;
import org.zeros.farm_manager_server.domain.entities.crop.MainCrop;
import org.zeros.farm_manager_server.domain.entities.data.Plant;
import org.zeros.farm_manager_server.domain.entities.data.Subside;
import org.zeros.farm_manager_server.domain.entities.fields.FieldPart;
import org.zeros.farm_manager_server.domain.entities.operations.AgriculturalOperation;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.exception.Enum.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.repositories.crop.CropRepository;
import org.zeros.farm_manager_server.services.interfaces.crop.CropManager;
import org.zeros.farm_manager_server.services.interfaces.data.PlantManager;
import org.zeros.farm_manager_server.services.interfaces.data.SubsideManager;
import org.zeros.farm_manager_server.services.interfaces.fields.FieldPartManager;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class CropManagerDefault implements CropManager {
    private final CropRepository cropRepository;
    private final SubsideManager subsideManager;
    private final FieldPartManager fieldPartManager;
    private final PlantManager plantManager;


    @Override
    @Transactional(readOnly = true)
    public CropDTO getCropById(UUID cropId) {
        Crop crop = cropRepository.findById(cropId).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(Crop.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
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

        return (MainCropDTO) getCropById(cropSaved.getId());
    }


    @Override
    @Transactional
    public InterCropDTO createNewInterCrop(UUID fieldPartId, Set<UUID> cultivatedPlantsIds) {
        FieldPart fieldPart = fieldPartManager.getFieldPartIfExists(fieldPartId);
        Set<Plant> cultivatedPlants = getPlantsIfExist(cultivatedPlantsIds);
        Crop crop = InterCrop.builder().cultivatedPlants(cultivatedPlants).fieldPart(fieldPart).build();
        Crop cropSaved = cropRepository.saveAndFlush(crop);
        fieldPart.getCrops().add(cropSaved);

        return (InterCropDTO) getCropById(cropSaved.getId());
    }

    @Override
    @Transactional
    public void deleteCropAndItsData(UUID cropId) {
        Crop crop = cropRepository.findById(cropId).orElse(MainCrop.NONE);
        if (crop == MainCrop.NONE) {
            return;
        }
        cropRepository.delete(crop);

    }

    @Override
    @Transactional
    public void updateCultivatedPlants(UUID cropId, Set<UUID> cultivatedPlantsIds) {
        Crop crop = getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        Set<Plant> cultivatedPlants = getPlantsIfExist(cultivatedPlantsIds);
        crop.setCultivatedPlants(cultivatedPlants);

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

        }
        throw new IllegalArgumentExceptionCustom(Crop.class, IllegalArgumentExceptionCause.TYPE_MISMATCH);
    }

    @Override
    @Transactional
    public void setWorkFinished(UUID cropId) {
        Crop crop = getCropIfExists(cropId);
        removePlannedOperations(crop);
        crop.setWorkFinished(true);

        getCropIfExists(crop.getId());
    }

    private void removePlannedOperations(Crop crop) {
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

    }

    @Override
    @Transactional
    public void removeSubside(UUID cropId, UUID subsideId) {
        Crop crop = getCropIfExists(cropId);
        checkOperationModificationAccess(crop);
        Subside subside = subsideManager.getSubsideIfExists(subsideId);
        crop.getSubsides().remove(subside);

    }

    @Override
    @Transactional
    public Crop getCropIfExists(UUID cropId) {
        if (cropId == null) {
            throw new IllegalArgumentExceptionCustom(Crop.class, Set.of("id"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        return cropRepository.findById(cropId).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(Crop.class,
                        IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
    }
}
