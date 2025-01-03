package org.zeros.farm_manager_server.Domain.Mappers.Crop.Plant;

import org.mapstruct.*;
import org.zeros.farm_manager_server.Domain.DTO.Crop.Plant.PlantDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Plant;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        typeConversionPolicy = ReportingPolicy.ERROR)
public interface PlantMapper extends DtoFromEntityMapper<PlantDTO, Plant> {
}
