package org.zeros.farm_manager_server.Services.Default.Data;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.CustomException.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.CustomException.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Domain.DTO.Crop.SubsideDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Subside;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.Repositories.Data.SpeciesRepository;
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
    public Page<Subside> getSubsidesByNameAs(@NotNull String name, int pageNumber) {
        return subsideRepository.findAllByNameContainingIgnoreCaseAndCreatedByIn(
                name, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Subside> getSubsidesBySpeciesAllowed(@NotNull Species species, int pageNumber) {
        return subsideRepository.findAllBySpeciesAllowedContainsAndCreatedByIn(
                species, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Subside getSubsideById(@NotNull UUID id) {
        return subsideRepository.getSubsideById(id).orElse(Subside.NONE);
    }

    @Override
    public Subside addSubside(@NotNull SubsideDTO subsideDTO) {
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
        if(dto.getSpeciesAllowed()==null||dto.getSpeciesAllowed().isEmpty()){
            entityParsed.setSpeciesAllowed(Set.of(speciesManager.getUndefinedSpecies()));
        }else {
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
    public Subside updateSubside(@NotNull SubsideDTO subsideDTO) {
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
    public void deleteSubsideSafe(@NotNull UUID subsideId) {
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
