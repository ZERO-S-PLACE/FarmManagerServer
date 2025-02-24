package org.zeros.farm_manager_server.domain.mappers.crop.crop_parameters;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.zeros.farm_manager_server.domain.dto.crop.CropParameters.SugarBeetParametersDTO;
import org.zeros.farm_manager_server.domain.entities.crop.crop_parameters.SugarBeetParameters;
import org.zeros.farm_manager_server.domain.mappers.DtoFromEntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        typeConversionPolicy = ReportingPolicy.ERROR)
public interface SugarBeetParametersMapper extends DtoFromEntityMapper<SugarBeetParametersDTO, SugarBeetParameters> {
}
