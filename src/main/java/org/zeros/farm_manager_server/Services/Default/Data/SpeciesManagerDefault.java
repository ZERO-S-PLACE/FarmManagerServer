package org.zeros.farm_manager_server.Services.Default.Data;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Exception.Enum.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.Exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Domain.DTO.Data.SpeciesDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.Data.PlantRepository;
import org.zeros.farm_manager_server.Repositories.Data.SpeciesRepository;
import org.zeros.farm_manager_server.Repositories.Data.SubsideRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;

import java.util.Set;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class SpeciesManagerDefault implements SpeciesManager {

    private final PlantRepository plantRepository;
    private final SpeciesRepository speciesRepository;
    private final SubsideRepository subsideRepository;
    private final LoggedUserConfiguration config;


    private static PageRequest getPageRequest(int pageNumber) {
        if (pageNumber < 0) pageNumber = 0;
        return PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name"));
    }

    @Override
    public Page<Species> getAllSpecies(int pageNumber) {
        return speciesRepository.findAllByCreatedByIn(config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Species> getDefaultSpecies(int pageNumber) {
        return speciesRepository.findAllByCreatedByIn(config.defaultRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Species> getUserSpecies(int pageNumber) {
        return speciesRepository.findAllByCreatedByIn(config.userRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Species> getSpeciesByNameAs(String name, int pageNumber) {
        return speciesRepository.findAllByNameContainsIgnoreCaseAndCreatedByIn(name,
                config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Species> getSpeciesByFamilyAs(String family, int pageNumber) {
        return speciesRepository.findAllByFamilyContainsIgnoreCaseAndCreatedByIn(family,
                config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Species> getSpeciesCriteria(String name, String family, int pageNumber) {
        boolean nameNotPresent = name == null || name.isEmpty();
        boolean familyNotPresent = family == null || family.isEmpty();

        if (nameNotPresent && !familyNotPresent) {
            return getSpeciesByFamilyAs(family, pageNumber);
        }
        if (!nameNotPresent && familyNotPresent) {
            return getSpeciesByNameAs(name, pageNumber);
        }
        return getAllSpecies(pageNumber);
    }

    @Override
    public Species getSpeciesById(UUID id) {
        return speciesRepository.findById(id).orElse(Species.NONE);
    }

    @Override
    public Species addSpecies(SpeciesDTO speciesDTO) {
        checkIfRequiredFieldsPresent(speciesDTO);
        checkIfUnique(speciesDTO);
        return speciesRepository.saveAndFlush(rewriteValuesToEntity(speciesDTO, Species.NONE));
    }

    private void checkIfRequiredFieldsPresent(SpeciesDTO speciesDTO) {
        if (speciesDTO.getName() == null || speciesDTO.getFamily() == null
                || speciesDTO.getName().isBlank() || speciesDTO.getFamily().isBlank()) {
            throw new IllegalArgumentExceptionCustom(Species.class,
                    Set.of("name", "family"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
    }

    private void checkIfUnique(SpeciesDTO speciesDTO) {
        if (speciesDTO.getId() == null && speciesRepository.findByNameAndFamilyAndCreatedByIn(speciesDTO.getName(),
                speciesDTO.getFamily(), config.allRows()).isPresent()) {
            throw new IllegalArgumentExceptionCustom(Species.class,
                    IllegalArgumentExceptionCause.OBJECT_EXISTS);
        }
    }

    private Species rewriteValuesToEntity(SpeciesDTO dto, Species entity) {
        Species entityParsed = DefaultMappers.speciesMapper.dtoToEntitySimpleProperties(dto);
        entityParsed.setCreatedBy(config.username());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        return entityParsed;
    }

    @Override
    public Species updateSpecies(SpeciesDTO speciesDTO) {
        Species originalSpecies = getSpeciesIfExists(speciesDTO);
        checkAccess(originalSpecies);
        checkIfRequiredFieldsPresent(speciesDTO);
        return speciesRepository.saveAndFlush(rewriteValuesToEntity(speciesDTO, originalSpecies));
    }

    private void checkAccess(Species originalSpecies) {
        if (originalSpecies.getCreatedBy().equals(config.username())) {
            return;
        }
        throw new IllegalAccessErrorCustom(Species.class,
                IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
    }

    private Species getSpeciesIfExists(SpeciesDTO speciesDTO) {
        if (speciesDTO.getId() == null) {
            throw new IllegalArgumentExceptionCustom(Species.class, Set.of("Id"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        Species originalSpecies = speciesRepository.findById(speciesDTO.getId()).orElse(Species.NONE);
        if (originalSpecies.equals(Species.NONE)) {
            throw new IllegalArgumentExceptionCustom(Species.class,
                    IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return originalSpecies;
    }

    @Override
    public void deleteSpeciesSafe(UUID speciesId) {
        Species originalSpecies = getSpeciesById(speciesId);
        if (originalSpecies == Species.NONE) {
            return;
        }
        checkAccess(originalSpecies);
        checkUsages(originalSpecies);
        speciesRepository.delete(originalSpecies);
    }

    @Override
    public Species getUndefinedSpecies() {
        return speciesRepository.getSpeciesByName("ANY").orElse(Species.ANY);
    }

    private void checkUsages(Species originalSpecies) {
        if (plantRepository.findAllBySpeciesAndCreatedByIn(originalSpecies,
                config.allRows(), PageRequest.of(0, 1)).isEmpty()
                && subsideRepository.findAllBySpeciesAllowedContains(originalSpecies).isEmpty()) {
            return;
        }
        throw new IllegalAccessErrorCustom(Species.class,
                IllegalAccessErrorCause.USAGE_IN_OTHER_PLACES);
    }

}
