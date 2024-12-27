package org.zeros.farm_manager_server.DAO.Default.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.Data.SprayManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Enum.SprayType;
import org.zeros.farm_manager_server.model.ApplicationDefaults;
import org.zeros.farm_manager_server.repositories.AgriculturalOperation.SprayApplicationRepository;
import org.zeros.farm_manager_server.repositories.Data.SprayRepository;

import java.util.UUID;

@Component
public class SprayManagerDefault implements SprayManager {

    private final LoggedUserConfiguration config;
    private final SprayRepository sprayRepository;
    private final SprayApplicationRepository sprayApplicationRepository;

    public SprayManagerDefault(LoggedUserConfiguration loggedUserConfiguration, @Autowired SprayRepository sprayRepository, SprayApplicationRepository sprayApplicationRepository) {
        this.sprayRepository = sprayRepository;
        this.sprayApplicationRepository = sprayApplicationRepository;
        this.config = loggedUserConfiguration;
    }

    @Override
    public Page<Spray> getAllSprays(int pageNumber) {
        return sprayRepository.findAllByCreatedByIn(config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }

    @Override
    public Page<Spray> getDefaultSprays(int pageNumber) {
        return sprayRepository.findAllByCreatedByIn(config.defaultRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }

    @Override
    public Page<Spray> getUserSprays(int pageNumber) {
        return sprayRepository.findAllByCreatedByIn(config.userRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }

    @Override
    public Page<Spray> getSpraysByNameAs(String name, int pageNumber) {
        return sprayRepository.findAllByNameContainingIgnoreCaseAndCreatedByIn(name, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Spray> getSpraysByProducerAs(String producer, int pageNumber) {
        return sprayRepository.findAllByProducerContainingIgnoreCaseAndCreatedByIn(producer, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Spray> getSpraysBySprayType(SprayType sprayType, int pageNumber) {
        return sprayRepository.findAllBySprayTypeAndCreatedByIn(sprayType, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
    }

    @Override
    public Page<Spray> getSpraysByActiveSubstance(String activeSubstance, int pageNumber) {
        return sprayRepository.findAllByActiveSubstancesContainsAndCreatedByIn(activeSubstance, config.allRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("name")));
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
            return sprayRepository.saveAndFlush(spray);
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
}
