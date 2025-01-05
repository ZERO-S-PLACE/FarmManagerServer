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
import org.zeros.farm_manager_server.Domain.DTO.Data.SubsideDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;
import org.zeros.farm_manager_server.Domain.Entities.Data.Subside;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.Repositories.Data.SubsideRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;
import org.zeros.farm_manager_server.Services.Interface.Data.SubsideManager;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class SubsideManagerDefault implements SubsideManager {
    private final LoggedUserConfiguration config;
    private final SubsideRepository subsideRepository;
    private final CropRepository cropRepository;
    private final SpeciesManager speciesManager;


    private static PageRequest getPageRequest(int pageNumber) {
        if (pageNumber < 0) pageNumber = 0;
        return PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name").descending());
    }

    @Override
    public Page<Subside> getAllSubsides(int pageNumber) {
        return subsideRepository.findAllByCreatedByIn(config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Subside> getDefaultSubsides(int pageNumber) {
        return subsideRepository.findAllByCreatedByIn(config.defaultRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Subside> getUserSubsides(int pageNumber) {
        return subsideRepository.findAllByCreatedByIn(config.userRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Subside> getSubsidesByNameAs(String name, int pageNumber) {
        return subsideRepository.findAllByNameContainingIgnoreCaseAndCreatedByIn(
                name, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Subside> getSubsidesBySpeciesAllowed(UUID speciesId, int pageNumber) {
        Species species = speciesManager.getSpeciesById(speciesId);
        if (species.equals(Species.NONE)) {
            return Page.empty();
        }
        return getSubsidesBySpeciesAllowed(species, pageNumber);
    }

    public Page<Subside> getSubsidesBySpeciesAllowed(Species species, int pageNumber) {
        return subsideRepository.findAllBySpeciesAllowedContainsAndCreatedByIn(
                species, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Subside> getSubsidesByNameAsAndSpeciesAllowed(String name, UUID speciesId, int pageNumber) {
        Species species = speciesManager.getSpeciesById(speciesId);
        if (species.equals(Species.NONE)) {
            return Page.empty();
        }
        return getSubsidesByNameAsAndSpeciesAllowed(name, species, pageNumber);
    }

    private Page<Subside> getSubsidesByNameAsAndSpeciesAllowed(String name, Species species, int pageNumber) {
        return subsideRepository.findAllByNameContainingIgnoreCaseAndSpeciesAllowedContainsAndCreatedByIn(
                name, species, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Subside> getSubsidesCriteria(String name, UUID speciesId, int pageNumber) {
        boolean nameNotPresent = name == null || name.isEmpty();
        Species species = Species.NONE;
        if (speciesId != null) {
            species = speciesManager.getSpeciesById(speciesId);
        }
        boolean speciesNotPresent = species == Species.NONE;
        if (speciesNotPresent && nameNotPresent) {
            return getAllSubsides(pageNumber);
        }
        if (speciesNotPresent) {
            return getSubsidesByNameAs(name, pageNumber);
        }
        if (nameNotPresent) {
            return getSubsidesBySpeciesAllowed(species, pageNumber);
        }
        return getSubsidesByNameAsAndSpeciesAllowed(name, species, pageNumber);
    }

    @Override
    public Subside getSubsideById(UUID id) {
        return subsideRepository.getSubsideById(id).orElse(Subside.NONE);
    }

    @Override
    public Subside addSubside(SubsideDTO subsideDTO) {
        checkIfRequiredFieldsPresent(subsideDTO);
        checkIfUnique(subsideDTO);
        return subsideRepository.saveAndFlush(rewriteValuesToEntity(subsideDTO, Subside.NONE));
    }

    private void checkIfRequiredFieldsPresent(SubsideDTO subsideDTO) {
        if (subsideDTO.getName() == null || subsideDTO.getName().isBlank()
                || subsideDTO.getYearOfSubside() == null || subsideDTO.getYearOfSubside() == ApplicationDefaults.UNDEFINED_DATE_MIN) {
            throw new IllegalArgumentExceptionCustom(Subside.class,
                    Set.of("name", "yearOfSubside"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
    }

    private void checkIfUnique(SubsideDTO subsideDTO) {
        if (subsideDTO.getId() == null && subsideRepository.findByNameAndYearOfSubsideAndCreatedByIn(subsideDTO.getName(), subsideDTO.getYearOfSubside(),
                config.allRows(), getPageRequest(0)).isEmpty()) {
            return;
        }
        throw new IllegalArgumentExceptionCustom(Subside.class,
                IllegalArgumentExceptionCause.OBJECT_EXISTS);
    }


    private Subside rewriteValuesToEntity(SubsideDTO dto, Subside entity) {
        Subside entityParsed = DefaultMappers.subsideMapper.dtoToEntitySimpleProperties(dto);
        if (dto.getSpeciesAllowed() == null || dto.getSpeciesAllowed().isEmpty()) {
            entityParsed.setSpeciesAllowed(Set.of(speciesManager.getUndefinedSpecies()));
        } else {
            entityParsed.setSpeciesAllowed(dto.getSpeciesAllowed().stream()
                    .map(speciesManager::getSpeciesById).collect(Collectors.toSet()));
        }
        entityParsed.setCreatedBy(config.username());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        return entityParsed;
    }

    @Override
    public Subside updateSubside(SubsideDTO subsideDTO) {
        Subside originalSubside = getSubsideIfExists(subsideDTO);
        checkAccess(originalSubside);
        checkIfRequiredFieldsPresent(subsideDTO);
        return subsideRepository.saveAndFlush(rewriteValuesToEntity(subsideDTO, originalSubside));

    }

    private Subside getSubsideIfExists(SubsideDTO subsideDTO) {
        if (subsideDTO.getId() == null) {
            throw new IllegalArgumentExceptionCustom(
                    Subside.class,
                    Set.of("Id"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        Subside originalSubside = getSubsideById(subsideDTO.getId());
        if (originalSubside.equals(Subside.NONE)) {
            throw new IllegalArgumentExceptionCustom(
                    Subside.class,
                    IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return originalSubside;
    }

    private void checkAccess(Subside subside) {
        if (subside.getCreatedBy().equals(config.username())) {
            return;
        }
        throw new IllegalAccessErrorCustom(Subside.class,
                IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
    }

    @Override
    public void deleteSubsideSafe(UUID subsideId) {
        Subside originalSubside = getSubsideById(subsideId);
        if (originalSubside.equals(Subside.NONE)) {
            return;
        }
        checkAccess(originalSubside);
        checkUsages(originalSubside);
        subsideRepository.delete(originalSubside);
    }

    private void checkUsages(Subside originalSubside) {
        if (cropRepository.findAllBySubsidesContains(originalSubside).isEmpty()) {
            return;
        }
        throw new IllegalAccessErrorCustom(Species.class,
                IllegalAccessErrorCause.USAGE_IN_OTHER_PLACES);
    }
}
