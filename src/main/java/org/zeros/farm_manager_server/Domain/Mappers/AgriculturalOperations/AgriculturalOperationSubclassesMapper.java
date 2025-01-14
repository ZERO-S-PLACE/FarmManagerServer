package org.zeros.farm_manager_server.Domain.Mappers.AgriculturalOperations;

import org.zeros.farm_manager_server.Domain.DTO.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.Operations.*;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;

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
