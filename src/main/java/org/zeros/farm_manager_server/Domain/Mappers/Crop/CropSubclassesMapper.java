package org.zeros.farm_manager_server.Domain.Mappers.Crop;

import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.InterCropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.MainCropDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.InterCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;

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
