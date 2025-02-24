package org.zeros.farm_manager_server.services.default_impl.data;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.domain.dto.data.FertilizerDTO;
import org.zeros.farm_manager_server.domain.entities.data.Fertilizer;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.exception.Enum.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.model.ApplicationDefaults;
import org.zeros.farm_manager_server.repositories.operations.FertilizerApplicationRepository;
import org.zeros.farm_manager_server.repositories.operations.SprayApplicationRepository;
import org.zeros.farm_manager_server.repositories.data.FertilizerRepository;
import org.zeros.farm_manager_server.services.interfaces.data.FertilizerManager;

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
    @Transactional(readOnly = true)
    public Page<FertilizerDTO> getAllFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByCreatedByIn(
                        config.allRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.fertilizerMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FertilizerDTO> getDefaultFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByCreatedByIn(
                        config.defaultRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.fertilizerMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FertilizerDTO> getUserFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByCreatedByIn(
                        config.userRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.fertilizerMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FertilizerDTO> getFertilizerByNameAs(String name, int pageNumber) {
        return fertilizerRepository.findAllByNameContainingAndCreatedByIn(
                        name, config.allRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.fertilizerMapper::entityToDto);

    }

    @Override
    @Transactional(readOnly = true)
    public Page<FertilizerDTO> getNaturalFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByIsNaturalFertilizerAndCreatedByIn(
                        true, config.allRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.fertilizerMapper::entityToDto);

    }

    @Override
    @Transactional(readOnly = true)
    public Page<FertilizerDTO> getSyntheticFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByIsNaturalFertilizerAndCreatedByIn(
                        false, config.allRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.fertilizerMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FertilizerDTO> getFertilizersCriteria(String name, Boolean isNatural, int pageNumber) {
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
    @Transactional(readOnly = true)
    public FertilizerDTO getFertilizerById(UUID id) {
        Fertilizer fertilizer = fertilizerRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(Fertilizer.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
        return DefaultMappers.fertilizerMapper.entityToDto(fertilizer);
    }

    @Override
    @Transactional
    public FertilizerDTO addFertilizer(FertilizerDTO fertilizerDTO) {
        checkIfRequiredFieldsPresent(fertilizerDTO);
        checkIfUnique(fertilizerDTO);
        Fertilizer fertilizer = fertilizerRepository.saveAndFlush(
                rewriteValuesToEntity(fertilizerDTO, Fertilizer.NONE));
        return DefaultMappers.fertilizerMapper.entityToDto(fertilizer);
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
        if (fertilizerDTO.getId() == null && fertilizerRepository.findByNameAndProducerAndCreatedByIn(fertilizerDTO.getName()
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
    @Transactional
    public FertilizerDTO updateFertilizer(FertilizerDTO fertilizerDTO) {
        Fertilizer originalFertilizer = getFertilizerIfExists(fertilizerDTO.getId());
        checkAccess(originalFertilizer);
        checkIfRequiredFieldsPresent(fertilizerDTO);
        return DefaultMappers.fertilizerMapper.entityToDto(fertilizerRepository.saveAndFlush(
                rewriteValuesToEntity(fertilizerDTO, originalFertilizer)));

    }

    private void checkAccess(Fertilizer originalFertilizer) {
        if (originalFertilizer.getCreatedBy().equals(config.username())) {
            return;
        }
        throw new IllegalAccessErrorCustom(Fertilizer.class,
                IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
    }

    @Override
    @Transactional
    public Fertilizer getFertilizerIfExists(UUID fertilizerId) {
        if (fertilizerId == null) {
            throw new IllegalArgumentExceptionCustom(Fertilizer.class, Set.of("Id"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        return fertilizerRepository.findById(fertilizerId).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(Fertilizer.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
    }

    @Override
    @Transactional
    public void deleteFertilizerSafe(UUID fertilizerId) {
        Fertilizer originalFertilizer = fertilizerRepository.findById(fertilizerId).orElse(Fertilizer.NONE);
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
    @Transactional(readOnly = true)
    public Fertilizer getUndefinedFertilizer() {
        return fertilizerRepository.findByNameAndProducerAndCreatedByIn("UNDEFINED", "UNDEFINED",
                config.defaultRows()).orElse(Fertilizer.NONE);
    }

}
