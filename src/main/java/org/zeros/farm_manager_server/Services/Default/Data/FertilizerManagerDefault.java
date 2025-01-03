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
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.FertilizerDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.FertilizerApplicationRepository;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.SprayApplicationRepository;
import org.zeros.farm_manager_server.Repositories.Data.FertilizerRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.FertilizerManager;

import java.util.Set;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class FertilizerManagerDefault implements FertilizerManager {
    private final LoggedUserConfiguration config;
    private final FertilizerRepository fertilizerRepository;
    private final FertilizerApplicationRepository fertilizerApplicationRepository;
    private final SprayApplicationRepository sprayApplicationRepository;

    private static PageRequest getPageRequest(int pageNumber) {
        if (pageNumber < 0) pageNumber = 0;
        return PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name"));
    }


    @Override
    public Page<Fertilizer> getAllFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByCreatedByIn(
                config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Fertilizer> getDefaultFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByCreatedByIn(
                config.defaultRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Fertilizer> getUserFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByCreatedByIn(
                config.userRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Fertilizer> getFertilizerByNameAs(@NotNull String name, int pageNumber) {
        return fertilizerRepository.findAllByNameContainingAndCreatedByIn(
                name, config.allRows(), getPageRequest(pageNumber));

    }

    @Override
    public Page<Fertilizer> getNaturalFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByIsNaturalFertilizerAndCreatedByIn(
                true, config.allRows(), getPageRequest(pageNumber));

    }

    @Override
    public Page<Fertilizer> getSyntheticFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByIsNaturalFertilizerAndCreatedByIn(
                false, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Fertilizer> getFertilizersCriteria(@NotNull String name, @NotNull Boolean isNatural, int pageNumber) {
        if (name == null || name.isBlank()) {

            if (isNatural == null) {
                return getAllFertilizers(pageNumber);
            }
            if (isNatural) {
                return getNaturalFertilizers(pageNumber);
            }
            return getSyntheticFertilizers(pageNumber);

        }
        return getFertilizerByNameAs(name, pageNumber);
    }

    @Override
    public Fertilizer getFertilizerById(@NotNull UUID id) {
        return fertilizerRepository.findById(id).orElse(Fertilizer.NONE);
    }

    @Override
    public Fertilizer addFertilizer(@NotNull FertilizerDTO fertilizerDTO) {
        checkIfRequiredFieldsPresent(fertilizerDTO);
        checkIfUnique(fertilizerDTO);
        return fertilizerRepository.saveAndFlush(
                rewriteValuesToEntity(fertilizerDTO, Fertilizer.NONE));
    }

    private void checkIfRequiredFieldsPresent(FertilizerDTO fertilizerDTO) {
        if (fertilizerDTO.getName() == null || fertilizerDTO.getName().isBlank()
                || fertilizerDTO.getIsNaturalFertilizer() == null) {
            throw new IllegalArgumentExceptionCustom(Fertilizer.class,
                    Set.of("isNaturalFertilizer", "name"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
    }

    private void checkIfUnique(FertilizerDTO fertilizerDTO) {
        if (fertilizerDTO.getId()==null&&fertilizerRepository.findByNameAndProducerAndCreatedByIn(fertilizerDTO.getName()
                , fertilizerDTO.getProducer(), config.allRows()).isEmpty()) {
            return;
        }
        throw new IllegalArgumentExceptionCustom(Fertilizer.class,
                IllegalArgumentExceptionCause.OBJECT_EXISTS);
    }

    private Fertilizer rewriteValuesToEntity(FertilizerDTO dto, Fertilizer entity) {
        Fertilizer entityParsed = DefaultMappers.fertilizerMapper.dtoToEntitySimpleProperties(dto);
        entityParsed.setCreatedBy(config.username());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        return entityParsed;
    }

    @Override
    public Fertilizer updateFertilizer(@NotNull FertilizerDTO fertilizerDTO) {

        Fertilizer originalFertilizer = getFertilizerIfExists(fertilizerDTO);
        checkAccess(originalFertilizer);
        checkIfRequiredFieldsPresent(fertilizerDTO);
        return fertilizerRepository.saveAndFlush(
                rewriteValuesToEntity(fertilizerDTO, originalFertilizer));

    }

    private void checkAccess(Fertilizer originalFertilizer) {
        if (originalFertilizer.getCreatedBy().equals(config.username())) {
            return;
        }
        throw new IllegalAccessErrorCustom(Fertilizer.class,
                IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
    }

    private Fertilizer getFertilizerIfExists(FertilizerDTO fertilizerDTO) {
        if (fertilizerDTO.getId() == null) {
            throw new IllegalArgumentExceptionCustom(
                    Fertilizer.class,
                    Set.of("Id"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        Fertilizer originalFertilizer = getFertilizerById(fertilizerDTO.getId());
        if (originalFertilizer.equals(Fertilizer.NONE)) {
            throw new IllegalArgumentExceptionCustom(
                    Fertilizer.class,
                    IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return originalFertilizer;
    }

    @Override
    public void deleteFertilizerSafe(@NotNull UUID fertilizerId) {
        Fertilizer originalFertilizer = getFertilizerById(fertilizerId);
        if (originalFertilizer.equals(Fertilizer.NONE)) {
            return;
        }
        checkAccess(originalFertilizer);
        checkUsages(originalFertilizer);
        fertilizerRepository.delete(originalFertilizer);
    }

    private void checkUsages(Fertilizer originalFertilizer) {
        if (fertilizerApplicationRepository.findAllByFertilizer(originalFertilizer).isEmpty() &&
                sprayApplicationRepository.findAllByFertilizer(originalFertilizer).isEmpty()) {
            return;
        }
        throw new IllegalAccessErrorCustom(Fertilizer.class,
                IllegalAccessErrorCause.USAGE_IN_OTHER_PLACES);
    }

    @Override
    public Fertilizer getUndefinedFertilizer() {
        return fertilizerRepository.findByNameAndProducerAndCreatedByIn("UNDEFINED", "UNDEFINED",
                config.defaultRows()).orElse(Fertilizer.NONE);
    }


}
