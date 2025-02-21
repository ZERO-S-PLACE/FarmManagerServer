package org.zeros.farm_manager_server.Domain.Mappers;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropSale;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Data.*;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.Operations.*;
import org.zeros.farm_manager_server.Domain.Entities.User.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component

public interface DtoFromEntityMapper<D extends BaseEntityDTO, E extends BaseEntity> {

    D entityToDto(@NotNull E entity);

    E dtoToEntitySimpleProperties(@NotNull D dto);

    default Set<UUID> mapEntitiesToIds(Set<? extends BaseEntity> value) {
        return value.stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }
    default UUID mapEntityToId(BaseEntity value) {
        return value.getId();
    }

    default Set<Crop> mapUUIDsToCrops(Set<UUID> value) {
        return new HashSet<>();
    }

    default Set<FieldGroup> mapUUIDsToFieldGroups(Set<UUID> value) {
        return new HashSet<>();
    }

    default Set<Plant> mapUUIDsToPlants(Set<UUID> value) {
        return new HashSet<>();
    }

    default Set<Seeding> mapUUIDsToSeeding(Set<UUID> value) {
        return new HashSet<>();
    }

    default Set<Cultivation> mapUUIDsToCultivations(Set<UUID> value) {
        return new HashSet<>();
    }

    default Set<SprayApplication> mapUUIDsToSprayApplications(Set<UUID> value) {
        return new HashSet<>();
    }

    default Set<FertilizerApplication> mapUUIDsToFertilizerApplications(Set<UUID> value) {
        return new HashSet<>();
    }

    default Set<Subside> mapUUIDsToSubsides(Set<UUID> value) {
        return new HashSet<>();
    }

    default Set<Harvest> mapUUIDsToHarvests(Set<UUID> value) {
        return new HashSet<>();
    }

    default Set<CropSale> mapUUIDsToCropSales(Set<UUID> value) {
        return new HashSet<>();
    }

    default Set<FieldPart> mapUUIDsToFieldParts(Set<UUID> value) {
        return new HashSet<>();
    }

    default User mapUUIDtoUser(UUID value) {
        return User.NONE;
    }

    default FieldGroup mapUUIDtoFieldGroup(UUID value) {
        return FieldGroup.NONE;
    }

    default Field mapUUIDtoField(UUID value) {
        return Field.NONE;
    }

    default FieldPart mapUUIDtoFieldPart(UUID value) {
        return FieldPart.NONE;
    }

    default Crop mapUUIDtoCrop(UUID value) {
        return MainCrop.NONE;
    }

    default CropParameters mapUUIDtoCropParameters(UUID value) {
        return CropParameters.NONE;
    }

    default FarmingMachine mapUUIDtoFarmingMachine(UUID value) {
        return FarmingMachine.NONE;
    }

    default Fertilizer mapUUIDtoFertilizer(UUID value) {
        return Fertilizer.NONE;
    }

    default Spray mapUUIDtoSpray(UUID value) {
        return Spray.NONE;
    }

    default Plant mapUUIDtoPlant(UUID value) {
        return Plant.NONE;
    }

    default Species mapUUIDtoSpecies(UUID value) {
        return Species.NONE;
    }

}
