package org.zeros.farm_manager_server.Domain.Mappers.Crop.CropParameters;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.RapeSeedParametersDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.RapeSeedParameters;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        typeConversionPolicy = ReportingPolicy.ERROR)
public interface RapeSeedParametersMapper extends DtoFromEntityMapper<RapeSeedParametersDTO, RapeSeedParameters> {
}
