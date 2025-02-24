package org.zeros.farm_manager_server.domain.mappers.crop.crop_parameters;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.zeros.farm_manager_server.domain.dto.crop.CropParameters.GrainParametersDTO;
import org.zeros.farm_manager_server.domain.entities.crop.crop_parameters.GrainParameters;
import org.zeros.farm_manager_server.domain.mappers.DtoFromEntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        typeConversionPolicy = ReportingPolicy.ERROR)
public interface GrainParametersMapper extends DtoFromEntityMapper<GrainParametersDTO, GrainParameters> {
}
