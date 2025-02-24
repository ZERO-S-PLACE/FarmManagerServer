package org.zeros.farm_manager_server.domain.mappers.data;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.zeros.farm_manager_server.domain.dto.data.SubsideDTO;
import org.zeros.farm_manager_server.domain.entities.data.Subside;
import org.zeros.farm_manager_server.domain.mappers.DtoFromEntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        typeConversionPolicy = ReportingPolicy.ERROR)
public interface SubsideMapper extends DtoFromEntityMapper<SubsideDTO, Subside> {
}
