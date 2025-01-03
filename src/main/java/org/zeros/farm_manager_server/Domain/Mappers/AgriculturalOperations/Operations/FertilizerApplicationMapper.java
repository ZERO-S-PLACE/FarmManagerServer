package org.zeros.farm_manager_server.Domain.Mappers.AgriculturalOperations.Operations;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Operations.FertilizerApplicationDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Operations.FertilizerApplication;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        typeConversionPolicy = ReportingPolicy.ERROR)
public interface FertilizerApplicationMapper extends DtoFromEntityMapper<FertilizerApplicationDTO, FertilizerApplication> {
}
