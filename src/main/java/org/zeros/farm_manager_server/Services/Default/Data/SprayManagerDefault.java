package org.zeros.farm_manager_server.Services.Default.Data;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.SprayType;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.SprayApplicationRepository;
import org.zeros.farm_manager_server.Repositories.Data.SprayRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.SprayManager;

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
        return sprayRepository.findAllByNameContainingIgnoreCaseAndCreatedByIn(name, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Spray> getSpraysByProducerAs(String producer, int pageNumber) {
        return sprayRepository.findAllByProducerContainingIgnoreCaseAndCreatedByIn(producer, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Spray> getSpraysBySprayType(SprayType sprayType, int pageNumber) {
        return sprayRepository.findAllBySprayTypeAndCreatedByIn(sprayType, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Spray> getSpraysByActiveSubstance(String activeSubstance, int pageNumber) {
        return sprayRepository.findAllByActiveSubstancesContainsAndCreatedByIn(activeSubstance, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Spray getSprayById(UUID uuid) {
        return sprayRepository.findById(uuid).orElse(Spray.NONE);
    }

    @Override
    public Spray addSpray(Spray spray) {
        if (spray.getName().isBlank()) {
            throw new IllegalArgumentException("Spray name must be specified");
        }
        if (sprayRepository.findByNameAndProducerAndCreatedByIn(spray.getName(), spray.getProducer(), config.allRows()).isPresent()) {
            throw new IllegalArgumentException("There is already spray with this name");
        }
        spray.setCreatedBy(config.username());
        return sprayRepository.saveAndFlush(spray);
    }

    @Override
    public Spray updateSpray(Spray spray) {
        Spray originalSpray = sprayRepository.findById(spray.getId()).orElse(Spray.NONE);
        if (originalSpray.equals(Spray.NONE)) {
            throw new IllegalArgumentException("Spray not found");
        }
        if (originalSpray.getCreatedBy().equals(config.username())) {
            if (spray.getName().isBlank()) {
                throw new IllegalArgumentException("Spray name must be specified");
            }
            originalSpray.setName(spray.getName());
            originalSpray.setProducer(spray.getProducer());
            originalSpray.setActiveSubstances(spray.getActiveSubstances());
            originalSpray.setSprayType(spray.getSprayType());
            originalSpray.setDescription(spray.getDescription());
            return sprayRepository.saveAndFlush(originalSpray);
        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }

    @Override
    public void deleteSpraySafe(Spray spray) {

        Spray originalSpray = sprayRepository.findById(spray.getId()).orElse(Spray.NONE);
        if (originalSpray.getCreatedBy().equals(config.username())) {
            if (sprayApplicationRepository.findAllBySpray(spray).isEmpty()) {
                sprayRepository.delete(spray);
                return;
            }

            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }

    @Override
    public Spray getUndefinedSpray() {
        return sprayRepository.findByNameAndProducerAndCreatedByIn("UNDEFINED", "UNDEFINED", config.defaultRows()).orElse(Spray.NONE);
    }

    @Override
    public Page<Spray> getSpraysCriteria(String name, String producer, SprayType sprayType, String activeSubstance, Integer pageNumber) {
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
}
