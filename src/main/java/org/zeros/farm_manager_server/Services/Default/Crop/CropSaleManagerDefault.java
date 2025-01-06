package org.zeros.farm_manager_server.Services.Default.Crop;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropSale;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.Exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Repositories.Crop.CropSaleRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropSaleManager;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropParametersManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class CropSaleManagerDefault implements CropSaleManager {
    private final EntityManager entityManager;
    private final CropSaleRepository cropSaleRepository;
    private final CropParametersManager cropParametersManager;
    private final CropManager cropManager;


    private void flushChanges() {
        entityManager.flush();
        entityManager.clear();
    }


    @Override
    public CropSale addCropSale(UUID cropId, CropSaleDTO cropSaleDTO) {
        Crop crop = cropManager.getCropById(cropId);
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
            CropParameters cropParameters = cropParametersManager.getCropParametersById(dto.getCropParameters());
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
    public CropSale updateCropSale(CropSaleDTO cropSaleDTO) {
        Crop crop = cropManager.getCropById(cropSaleDTO.getCrop());
        checkSaleModificationAccess(crop);
        CropSale cropSaleOriginal = getCropSaleIfExist(cropSaleDTO);
        return cropSaleRepository.saveAndFlush(rewriteToEntity(cropSaleDTO, cropSaleOriginal));
    }

    private CropSale getCropSaleIfExist(CropSaleDTO cropSaleDTO) {
        if (cropSaleDTO.getId() == null) {
            throw new IllegalArgumentExceptionCustom(CropSale.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        CropSale cropSaleOriginal = getCropSaleById(cropSaleDTO.getId());
        if (cropSaleOriginal.equals(CropSale.NONE)) {
            throw new IllegalArgumentExceptionCustom(CropSale.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return cropSaleOriginal;
    }

    @Override
    public void deleteCropSale(UUID cropSaleId) {
        CropSale cropSale = getCropSaleById(cropSaleId);
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
    public CropSale getCropSaleById(UUID id) {
        return cropSaleRepository.findById(id).orElse(CropSale.NONE);
    }

}
