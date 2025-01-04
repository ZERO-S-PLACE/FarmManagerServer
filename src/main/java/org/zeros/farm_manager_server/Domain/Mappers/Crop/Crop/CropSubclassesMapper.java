package org.zeros.farm_manager_server.Domain.Mappers.Crop.Crop;

import org.zeros.farm_manager_server.Controllers.CropDataReaderController;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Domain.DTO.Crop.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.Crop.InterCropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.Crop.MainCropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Operations.Harvest;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.InterCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.GrainParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.RapeSeedParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.SugarBeetParameters;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;

public class CropSubclassesMapper implements DtoFromEntityMapper<CropDTO, Crop> {

    @Override
    public CropDTO entityToDto(Crop entity) {
        return switch (entity) {
            case MainCrop mainCrop -> DefaultMappers.mainCropMapper.entityToDto(mainCrop);
            case InterCrop interCrop -> DefaultMappers.interCropMapper.entityToDto(interCrop);
            default ->
                    throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                            IllegalArgumentExceptionCause.TYPE_MISMATCH);
        };
    }

    @Override
    public Crop dtoToEntitySimpleProperties(CropDTO dto) {
        return switch (dto) {
            case MainCropDTO mainCrop -> DefaultMappers.mainCropMapper.dtoToEntitySimpleProperties(mainCrop);
            case InterCropDTO interCrop -> DefaultMappers.interCropMapper.dtoToEntitySimpleProperties(interCrop);
            default ->
                    throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class,
                            IllegalArgumentExceptionCause.TYPE_MISMATCH);
        };
    }
}
