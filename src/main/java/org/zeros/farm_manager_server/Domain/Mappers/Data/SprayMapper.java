package org.zeros.farm_manager_server.Domain.Mappers.Data;


import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.zeros.farm_manager_server.Domain.DTO.Data.SprayDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Spray;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        typeConversionPolicy = ReportingPolicy.ERROR)
public interface SprayMapper extends DtoFromEntityMapper<SprayDTO, Spray> {
}