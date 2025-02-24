package org.zeros.farm_manager_server.domain.mappers.operations;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.zeros.farm_manager_server.domain.dto.operations.FertilizerApplicationDTO;
import org.zeros.farm_manager_server.domain.entities.operations.FertilizerApplication;
import org.zeros.farm_manager_server.domain.mappers.DtoFromEntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        typeConversionPolicy = ReportingPolicy.ERROR)
public interface FertilizerApplicationMapper extends DtoFromEntityMapper<FertilizerApplicationDTO, FertilizerApplication> {
}
