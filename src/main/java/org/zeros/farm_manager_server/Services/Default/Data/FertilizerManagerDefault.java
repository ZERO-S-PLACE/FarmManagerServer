package org.zeros.farm_manager_server.Services.Default.Data;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Services.Interface.Data.FertilizerManager;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.FertilizerApplicationRepository;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.SprayApplicationRepository;
import org.zeros.farm_manager_server.Repositories.Data.FertilizerRepository;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class FertilizerManagerDefault implements FertilizerManager {
    private final LoggedUserConfiguration config;
    private final FertilizerRepository fertilizerRepository;
    private final FertilizerApplicationRepository fertilizerApplicationRepository;
    private final SprayApplicationRepository sprayApplicationRepository;



    @Override
    public Page<Fertilizer> getAllFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByCreatedByIn(config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Fertilizer> getDefaultFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByCreatedByIn(config.defaultRows(), getPageRequest(pageNumber));
    }

    private static PageRequest getPageRequest(int pageNumber) {
        if(pageNumber < 0) pageNumber=0;
        return PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name"));
    }

    @Override
    public Page<Fertilizer> getUserFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByCreatedByIn(config.userRows(), getPageRequest(pageNumber));
    }

    @Override
    public Page<Fertilizer> getFertilizerByNameAs(String name, int pageNumber) {
        return fertilizerRepository.findAllByNameContainingAndCreatedByIn(name, config.allRows(), getPageRequest(pageNumber));

    }

    @Override
    public Page<Fertilizer> getNaturalFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByIsNaturalFertilizerAndCreatedByIn(true, config.allRows(), getPageRequest(pageNumber));

    }

    @Override
    public Page<Fertilizer> getSyntheticFertilizers(int pageNumber) {
        return fertilizerRepository.findAllByIsNaturalFertilizerAndCreatedByIn(false, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Fertilizer getFertilizerById(UUID id) {
        return fertilizerRepository.findById(id).orElse(Fertilizer.NONE);
    }

    @Override
    public Fertilizer addFertilizer(Fertilizer fertilizer) {
        if (fertilizer.getName().isBlank()) {
            throw new IllegalArgumentException("Fertilizer name is required");
        }
        if (fertilizerRepository.findByNameAndProducerAndCreatedByIn(fertilizer.getName(), fertilizer.getProducer(), config.allRows()).isEmpty()) {
            fertilizer.setCreatedBy(config.username());
            return fertilizerRepository.saveAndFlush(fertilizer);
        }
        throw new IllegalArgumentException("Fertilizer already exists");
    }

    @Override
    public Fertilizer updateFertilizer(Fertilizer fertilizer) {

        Fertilizer originalFertilizer = fertilizerRepository.findById(fertilizer.getId()).orElse(Fertilizer.NONE);
        if (originalFertilizer.equals(Fertilizer.NONE)) {
            throw new IllegalArgumentException("Fertilizer not found");
        }
        if (originalFertilizer.getCreatedBy().equals(config.username())) {
            if (fertilizer.getName().isBlank()) {
                throw new IllegalArgumentException("Fertilizer name is required");
            }
            fertilizer.setCreatedDate(originalFertilizer.getCreatedDate());
            fertilizer.setLastModifiedDate(originalFertilizer.getLastModifiedDate());
            return fertilizerRepository.saveAndFlush(fertilizer);
        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }

    @Override
    public void deleteFertilizerSafe(Fertilizer fertilizer) {
        Fertilizer originalFertilizer = fertilizerRepository.findById(fertilizer.getId()).orElse(Fertilizer.NONE);
        if (originalFertilizer.getCreatedBy().equals(config.username())) {
            if (fertilizerApplicationRepository.findAllByFertilizer(fertilizer).isEmpty()) {
                if (sprayApplicationRepository.findAllByFertilizer(fertilizer).isEmpty()) {
                    fertilizerRepository.delete(fertilizer);
                    return;
                }
            }
            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }

    @Override
    public Fertilizer getUndefinedFertilizer() {
        return fertilizerRepository.findByNameAndProducerAndCreatedByIn("UNDEFINED", "UNDEFINED", config.defaultRows()).orElse(Fertilizer.NONE);
    }

    @Override
    public Page<Fertilizer> getFertilizersCriteria(String name, Boolean isNatural, Integer pageNumber) {
        if(name==null||name.isBlank()){

            if(isNatural==null){
                return getAllFertilizers(pageNumber);
            }
            if(isNatural){
                return getNaturalFertilizers(pageNumber);
            }
            return getSyntheticFertilizers(pageNumber);

        }
        return getFertilizerByNameAs(name,pageNumber);
    }
}
