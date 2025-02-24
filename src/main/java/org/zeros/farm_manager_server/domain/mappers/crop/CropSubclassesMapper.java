package org.zeros.farm_manager_server.domain.mappers.crop;

import org.zeros.farm_manager_server.domain.dto.crop.CropDTO;
import org.zeros.farm_manager_server.domain.dto.crop.InterCropDTO;
import org.zeros.farm_manager_server.domain.dto.crop.MainCropDTO;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;
import org.zeros.farm_manager_server.domain.entities.crop.InterCrop;
import org.zeros.farm_manager_server.domain.entities.crop.MainCrop;
import org.zeros.farm_manager_server.domain.entities.operations.AgriculturalOperation;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.domain.mappers.DtoFromEntityMapper;
import org.zeros.farm_manager_server.exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.exception.IllegalArgumentExceptionCustom;

public class CropSubclassesMapper implements DtoFromEntityMapper<CropDTO, Crop> {

    @Override
    public CropDTO entityToDto(Crop entity) {
        return switch (entity) {
            case MainCrop mainCrop -> DefaultMappers.mainCropMapper.entityToDto(mainCrop);
            case InterCrop interCrop -> DefaultMappers.interCropMapper.entityToDto(interCrop);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        };
    }

    @Override
    public Crop dtoToEntitySimpleProperties(CropDTO dto) {
        return switch (dto) {
            case MainCropDTO mainCrop -> DefaultMappers.mainCropMapper.dtoToEntitySimpleProperties(mainCrop);
            case InterCropDTO interCrop -> DefaultMappers.interCropMapper.dtoToEntitySimpleProperties(interCrop);
            default -> throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                    IllegalArgumentExceptionCause.TYPE_MISMATCH);
        };
    }
}
