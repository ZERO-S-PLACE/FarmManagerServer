package org.zeros.farm_manager_server.Domain.Mappers.CropParameters;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.zeros.farm_manager_server.Domain.DTO.CropParameters.SugarBeetParametersDTO;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.SugarBeetParameters;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        typeConversionPolicy = ReportingPolicy.ERROR)
public interface SugarBeetParametersMapper extends DtoFromEntityMapper<SugarBeetParametersDTO, SugarBeetParameters> {
}