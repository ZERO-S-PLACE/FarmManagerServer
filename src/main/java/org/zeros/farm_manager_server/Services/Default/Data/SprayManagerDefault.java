package org.zeros.farm_manager_server.Services.Default.Data;

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
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.SprayDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.SprayType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.SprayApplicationRepository;
import org.zeros.farm_manager_server.Repositories.Data.SprayRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.SprayManager;

import java.util.Set;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class SprayManagerDefault implements SprayManager {

    private final LoggedUserConfiguration config;
    private final SprayRepository sprayRepository;
    private final SprayApplicationRepository sprayApplicationRepository;


    private static PageRequest getPageRequest(int pageNumber) {
        if (pageNumber < 0) pageNumber = 0;
        return PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name"));
    }

    @Override
    public Page<Spray> getAllSprays(int pageNumber) {
        return sprayRepository.findAllByCreatedByIn(config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Spray> getDefaultSprays(int pageNumber) {
        return sprayRepository.findAllByCreatedByIn(config.defaultRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Spray> getUserSprays(int pageNumber) {
        return sprayRepository.findAllByCreatedByIn(config.userRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Spray> getSpraysByNameAs(String name, int pageNumber) {
        return sprayRepository.findAllByNameContainingIgnoreCaseAndCreatedByIn(name,
                config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Spray> getSpraysByProducerAs(String producer, int pageNumber) {
        return sprayRepository.findAllByProducerContainingIgnoreCaseAndCreatedByIn(producer,
                config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Spray> getSpraysBySprayType(SprayType sprayType, int pageNumber) {
        return sprayRepository.findAllBySprayTypeAndCreatedByIn(sprayType,
                config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Spray> getSpraysByActiveSubstance(String activeSubstance, int pageNumber) {
        return sprayRepository.findAllByActiveSubstancesContainsAndCreatedByIn(activeSubstance,
                config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Spray> getSpraysCriteria(String name, String producer, SprayType sprayType,
                                         String activeSubstance, Integer pageNumber) {
        boolean nameNotPresent = name == null || name.isBlank();
        boolean producerNotPresent = producer == null || producer.isBlank();
        boolean sprayTypeNotPresent = sprayType == null || sprayType.equals(SprayType.NONE);
        boolean activeSubstanceNotPresent = activeSubstance == null || activeSubstance.isBlank();
        if (nameNotPresent) {
            if (producerNotPresent) {
                if (sprayTypeNotPresent) {
                    if (activeSubstanceNotPresent) {
                        return getAllSprays(pageNumber);
                    }
                    return getSpraysByActiveSubstance(activeSubstance, pageNumber);
                }
                return getSpraysBySprayType(sprayType, pageNumber);
            }
            return getSpraysByProducerAs(producer, pageNumber);
        }
        return getSpraysByNameAs(name, pageNumber);
    }

    @Override
    public Spray getSprayById(UUID uuid) {
        return sprayRepository.findById(uuid).orElse(Spray.NONE);
    }

    @Override
    public Spray addSpray(SprayDTO sprayDTO) {
        checkIfRequiredFieldsPresent(sprayDTO);
        checkIfUnique(sprayDTO);
        return sprayRepository.saveAndFlush(rewriteValuesToEntity(sprayDTO, Spray.NONE));
    }

    private void checkIfRequiredFieldsPresent(SprayDTO sprayDTO) {
        if (sprayDTO.getName() == null || sprayDTO.getName().isBlank() || sprayDTO.getSprayType() == null) {
            throw new IllegalArgumentExceptionCustom(Spray.class,
                    Set.of("name", "sprayType"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
    }

    private void checkIfUnique(SprayDTO sprayDTO) {
        if (sprayDTO.getId() == null && sprayRepository.findByNameAndSprayTypeAndCreatedByIn(
                sprayDTO.getName(), sprayDTO.getSprayType(), config.allRows()).isPresent()) {
            throw new IllegalArgumentExceptionCustom(Spray.class,
                    IllegalArgumentExceptionCause.OBJECT_EXISTS);
        }
    }

    private Spray rewriteValuesToEntity(SprayDTO dto, Spray entity) {
        Spray entityParsed = DefaultMappers.sprayMapper.dtoToEntitySimpleProperties(dto);
        entityParsed.setCreatedBy(config.username());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        return entityParsed;
    }

    @Override
    public Spray updateSpray(SprayDTO sprayDTO) {
        Spray originalSpray = getSprayIfExists(sprayDTO);
        checkAccess(originalSpray);
        checkIfRequiredFieldsPresent(sprayDTO);
        return sprayRepository.saveAndFlush(rewriteValuesToEntity(sprayDTO, originalSpray));
    }

    private Spray getSprayIfExists(SprayDTO sprayDTO) {
        if (sprayDTO.getId() == null) {
            throw new IllegalArgumentExceptionCustom(
                    Spray.class,
                    Set.of("Id"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        Spray originalSpray = getSprayById(sprayDTO.getId());
        if (originalSpray.equals(Spray.NONE)) {
            throw new IllegalArgumentExceptionCustom(
                    Spray.class,
                    IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return originalSpray;
    }

    private void checkAccess(Spray spray) {
        if (spray.getCreatedBy().equals(config.username())) {
            return;
        }
        throw new IllegalAccessErrorCustom(Spray.class,
                IllegalAccessErrorCause.USAGE_IN_OTHER_PLACES);
    }

    @Override
    public void deleteSpraySafe(UUID sprayId) {
        Spray originalSpray = getSprayById(sprayId);
        if (originalSpray == Spray.NONE) {
            return;
        }
        checkAccess(originalSpray);
        checkUsages(originalSpray);
        sprayRepository.delete(originalSpray);
    }

    private void checkUsages(Spray originalSpray) {
        if (sprayApplicationRepository.findAllBySpray(originalSpray).isEmpty()) {
            return;
        }
        throw new IllegalAccessErrorCustom(Species.class,
                IllegalAccessErrorCause.USAGE_IN_OTHER_PLACES);
    }

    @Override
    public Spray getUndefinedSpray() {
        return sprayRepository.findByNameAndSprayTypeAndCreatedByIn(
                "UNDEFINED", SprayType.OTHER, config.defaultRows()).orElse(Spray.NONE);
    }
}