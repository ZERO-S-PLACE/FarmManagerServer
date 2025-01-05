package org.zeros.farm_manager_server.Services.Default.Data;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Exception.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.Exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Domain.DTO.Data.PlantDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Plant;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.Repositories.Data.PlantRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.PlantManager;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;

import java.util.Set;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class PlantManagerDefault implements PlantManager {

    private final LoggedUserConfiguration config;
    private final PlantRepository plantRepository;
    private final CropRepository cropRepository;
    private final SpeciesManager speciesManager;


    private static PageRequest getPageRequest(int pageNumber) {
        return PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("variety"));
    }

    @Override
    public Page<Plant> getAllPlants(int pageNumber) {
        if (pageNumber < 0) pageNumber = 0;
        return plantRepository.findAllByCreatedByIn(config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Plant> getDefaultPlants(int pageNumber) {
        return plantRepository.findAllByCreatedByIn(config.defaultRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Plant> getUserPlants(int pageNumber) {
        return plantRepository.findAllByCreatedByIn(config.userRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Plant> getPlantsByVarietyAs(String variety, int pageNumber) {
        return plantRepository.findAllByVarietyContainingIgnoreCaseAndCreatedByIn(variety,
                config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Plant> getPlantsBySpecies(Species species, int pageNumber) {
        return plantRepository.findAllBySpeciesAndCreatedByIn(species, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Plant> getPlantsByVarietyAndSpecies(String variety, Species species, int pageNumber) {
        return plantRepository.findAllBySpeciesAndVarietyContainingIgnoreCaseAndCreatedByIn(species, variety,
                config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Plant> getPlantsCriteria(String variety, UUID speciesId, int pageNumber) {
        boolean varietyNotPresent = variety == null || variety.isEmpty();
        boolean speciesNotPresent;
        Species species = Species.NONE;
        if (speciesId != null) {
            species = speciesManager.getSpeciesById(speciesId);
        }
        speciesNotPresent = species.equals(Species.NONE);

        if (speciesNotPresent) {
            if (varietyNotPresent) {
                return getAllPlants(pageNumber);
            }
            return getPlantsByVarietyAs(variety, pageNumber);
        } else if (varietyNotPresent) {
            return getPlantsBySpecies(species, pageNumber);
        }
        return getPlantsByVarietyAndSpecies(variety, species, pageNumber);
    }

    @Override
    public Plant getPlantById(UUID uuid) {
        return plantRepository.findById(uuid).orElse(Plant.NONE);
    }

    @Override
    public Plant addPlant(PlantDTO plantDTO) {
        checkIfRequiredFieldsPresent(plantDTO);
        checkIfUnique(plantDTO);
        return plantRepository.saveAndFlush(rewriteValuesToEntity(plantDTO, Plant.NONE));
    }

    private void checkIfRequiredFieldsPresent(PlantDTO plantDTO) {
        if (plantDTO.getVariety() == null || plantDTO.getVariety().isBlank()
                || plantDTO.getSpecies() == null) {
            throw new IllegalArgumentExceptionCustom(Plant.class,
                    Set.of("variety", "species"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
    }

    private void checkIfUnique(PlantDTO plantDTO) {
        if (plantDTO.getId() == null && plantRepository.findAllBySpeciesAndVarietyAndCreatedByIn(
                speciesManager.getSpeciesById(plantDTO.getSpecies()),
                plantDTO.getVariety(),
                config.allRows(),
                getPageRequest(0)).isEmpty()) {
            return;
        }
        throw new IllegalArgumentExceptionCustom(Plant.class,
                IllegalArgumentExceptionCause.OBJECT_EXISTS);
    }

    private Plant rewriteValuesToEntity(PlantDTO dto, Plant entity) {
        Plant entityParsed = DefaultMappers.plantMapper.dtoToEntitySimpleProperties(dto);
        entityParsed.setCreatedBy(config.username());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        entityParsed.setSpecies(speciesManager.getSpeciesById(dto.getSpecies()));
        return entityParsed;

    }

    @Override
    public Plant updatePlant(PlantDTO plantDTO) {
        Plant originalPlant = getPlantIfExists(plantDTO);
        checkAccess(originalPlant);
        checkIfRequiredFieldsPresent(plantDTO);
        return plantRepository.saveAndFlush(rewriteValuesToEntity(plantDTO, originalPlant));
    }

    private void checkAccess(Plant originalPlant) {
        if (originalPlant.getCreatedBy().equals(config.username())) {
            return;
        }
        throw new IllegalAccessErrorCustom(Plant.class,
                IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
    }

    private Plant getPlantIfExists(PlantDTO plantDTO) {
        if (plantDTO.getId() == null) {
            throw new IllegalArgumentExceptionCustom(
                    Plant.class,
                    Set.of("Id"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        Plant originalPlant = plantRepository.findById(plantDTO.getId()).orElse(Plant.NONE);
        if (originalPlant.equals(Plant.NONE)) {
            throw new IllegalArgumentExceptionCustom(
                    Plant.class,
                    IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return originalPlant;
    }

    @Override
    public void deletePlantSafe(UUID plantId) {

        Plant originalPlant = getPlantById(plantId);
        if (originalPlant.equals(Plant.NONE)) {
            return;
        }
        checkAccess(originalPlant);
        checkUsages(originalPlant);
        plantRepository.delete(originalPlant);

    }

    private void checkUsages(Plant originalPlant) {
        if (cropRepository.findAllByCultivatedPlantsContains(originalPlant).isEmpty()) {
            return;
        }
        throw new IllegalAccessErrorCustom(Plant.class,
                IllegalAccessErrorCause.USAGE_IN_OTHER_PLACES);
    }


}
