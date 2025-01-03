package org.zeros.farm_manager_server.Domain.Mappers.AgriculturalOperations.Data;

import org.mapstruct.*;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.FarmingMachineDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        typeConversionPolicy = ReportingPolicy.ERROR)
public interface FarmingMachineMapper extends DtoFromEntityMapper<FarmingMachineDTO, FarmingMachine> {

}
