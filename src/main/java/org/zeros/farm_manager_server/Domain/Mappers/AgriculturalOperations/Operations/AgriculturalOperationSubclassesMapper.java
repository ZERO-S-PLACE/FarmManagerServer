package org.zeros.farm_manager_server.Domain.Mappers.AgriculturalOperations.Operations;

import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;

public class AgriculturalOperationSubclassesMapper implements DtoFromEntityMapper<AgriculturalOperationDTO, AgriculturalOperation> {
    @Override
    public AgriculturalOperationDTO entityToDto(AgriculturalOperation entity) {
        return switch (entity) {
            case Seeding seeding -> DefaultMappers.seedingMapper.entityToDto(seeding);
            case Cultivation cultivation -> DefaultMappers.cultivationMapper.entityToDto(cultivation);
            case FertilizerApplication fertilizerApplication ->
                    DefaultMappers.fertilizerApplicationMapper.entityToDto(fertilizerApplication);
            case SprayApplication sprayApplication ->
                    DefaultMappers.sprayApplicationMapper.entityToDto(sprayApplication);
            case Harvest harvest -> DefaultMappers.harvestMapper.entityToDto(harvest);
            default ->
                    throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class, IllegalArgumentExceptionCause.TYPE_MISMATCH);
        };
    }

    @Override
    public AgriculturalOperation dtoToEntitySimpleProperties(AgriculturalOperationDTO dto) {
        return switch (dto) {
            case SeedingDTO seeding -> DefaultMappers.seedingMapper.dtoToEntitySimpleProperties(seeding);
            case CultivationDTO cultivation ->
                    DefaultMappers.cultivationMapper.dtoToEntitySimpleProperties(cultivation);
            case FertilizerApplicationDTO fertilizerApplication ->
                    DefaultMappers.fertilizerApplicationMapper.dtoToEntitySimpleProperties(fertilizerApplication);
            case SprayApplicationDTO sprayApplication ->
                    DefaultMappers.sprayApplicationMapper.dtoToEntitySimpleProperties(sprayApplication);
            case HarvestDTO harvest -> DefaultMappers.harvestMapper.dtoToEntitySimpleProperties(harvest);
            default ->
                    throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class, IllegalArgumentExceptionCause.TYPE_MISMATCH);
        };
    }
}
