package org.zeros.farm_manager_server.Services.Default.Crop;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropSale;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Repositories.Crop.CropSaleRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropSaleManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class CropSaleManagerDefault implements CropSaleManager {
    private final EntityManager entityManager;
    private final CropSaleRepository cropSaleRepository;
    private final CropParametersManager cropParametersManager;
    private final CropManager cropManager;

    @Override
    @Transactional
    public CropSaleDTO addCropSale(UUID cropId, CropSaleDTO cropSaleDTO) {
        Crop crop = cropManager.getCropIfExists(cropId);
        if (crop instanceof MainCrop) {

            checkSaleModificationAccess(crop);
            checkIfUnique(cropSaleDTO);
            CropSale cropSale = rewriteToEntity(cropSaleDTO, CropSale.NONE);
            cropSale.setCrop(crop);
            CropSale cropSaleSaved = cropSaleRepository.saveAndFlush(cropSale);
            ((MainCrop) crop).getCropSales().add(cropSaleSaved);
            flushChanges();
            return getCropSaleById(cropSaleSaved.getId());
        }
        throw new IllegalArgumentExceptionCustom(Crop.class, IllegalArgumentExceptionCause.TYPE_MISMATCH);
    }

    private void checkIfUnique(CropSaleDTO cropSaleDTO) {
        if (cropSaleDTO.getId() != null) {
            throw new IllegalArgumentExceptionCustom(Crop.class, IllegalArgumentExceptionCause.OBJECT_EXISTS);
        }
    }

    private void checkSaleModificationAccess(Crop crop) {
        if (crop instanceof MainCrop) {
            if (((MainCrop) crop).getIsFullySold()) {
                throw new IllegalAccessErrorCustom(Crop.class, IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
            }
            return;
        }
        throw new IllegalArgumentExceptionCustom(Crop.class, IllegalArgumentExceptionCause.TYPE_MISMATCH);
    }

    private CropSale rewriteToEntity(CropSaleDTO dto, CropSale entity) {
        CropSale entityParsed = DefaultMappers.cropSaleMapper.dtoToEntitySimpleProperties(dto);
        if (dto.getCropParameters() == null) {
            entityParsed.setCropParameters(cropParametersManager.getUndefinedCropParameters());
        } else {
            CropParameters cropParameters = cropParametersManager.getCropParametersIfExist(dto.getCropParameters());
            if (cropParameters == CropParameters.NONE) {
                entityParsed.setCropParameters(cropParametersManager.getUndefinedCropParameters());
            } else {
                entityParsed.setCropParameters(cropParameters);
            }
        }
        entityParsed.setCrop(entity.getCrop());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        return entityParsed;
    }

    @Override
    @Transactional
    public CropSaleDTO updateCropSale(CropSaleDTO cropSaleDTO) {
        Crop crop = cropManager.getCropIfExists(cropSaleDTO.getCrop());
        checkSaleModificationAccess(crop);
        CropSale cropSaleOriginal = getCropSaleIfExist(cropSaleDTO);
        CropSale updated = cropSaleRepository.saveAndFlush(rewriteToEntity(cropSaleDTO, cropSaleOriginal));
        return DefaultMappers.cropSaleMapper.entityToDto(updated);
    }

    private CropSale getCropSaleIfExist(CropSaleDTO cropSaleDTO) {
        if (cropSaleDTO.getId() == null) {
            throw new IllegalArgumentExceptionCustom(CropSale.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return cropSaleRepository.findById(cropSaleDTO.getId()).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(CropSale.class,
                        IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
    }

    @Override
    @Transactional
    public void deleteCropSale(UUID cropSaleId) {
        CropSale cropSale = cropSaleRepository.findById(cropSaleId).orElse(CropSale.NONE);
        if (cropSale == CropSale.NONE) {
            return;
        }
        checkSaleModificationAccess(cropSale.getCrop());
        MainCrop crop = (MainCrop) cropSale.getCrop();
        crop.getCropSales().remove(cropSale);
        cropSaleRepository.delete(cropSale);
        flushChanges();
    }

    @Override
    @Transactional(readOnly = true)
    public CropSaleDTO getCropSaleById(UUID id) {
        CropSale cropSale = cropSaleRepository.findById(id).orElseThrow(() -> new IllegalArgumentExceptionCustom(
                CropSale.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
        return DefaultMappers.cropSaleMapper.entityToDto(cropSale);
    }

    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }


}
