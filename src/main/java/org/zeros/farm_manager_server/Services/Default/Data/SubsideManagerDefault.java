package org.zeros.farm_manager_server.Services.Default.Data;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Plant.Species;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Subside;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.Repositories.Data.SubsideRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.SubsideManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class SubsideManagerDefault implements SubsideManager {
    private final LoggedUserConfiguration config;
    private final SubsideRepository subsideRepository;
    private final CropRepository cropRepository;

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
    public Page<Subside> getSubsidesBySpeciesAllowed(Species species, int pageNumber) {
        return subsideRepository.findAllBySpeciesAllowedContainsAndCreatedByIn(
                species, config.allRows(), getPageRequest(pageNumber));
    }

    @Override
    public Subside getSubsideById(UUID id) {
        return subsideRepository.getSubsideById(id).orElse(Subside.NONE);
    }

    @Override
    public Subside addSubside(Subside subside) {
        if (subside.getName().isBlank()) {
            throw new IllegalArgumentException("Subside name must be specified");
        }
        if (subsideRepository.findByNameAndCreatedByIn(subside.getName(), config.allRows()).isPresent()) {
            throw new IllegalArgumentException("There is already a subside with this name");
        }
        subside.setCreatedBy(config.username());
        return subsideRepository.saveAndFlush(subside);
    }

    @Override
    public Subside updateSubside(Subside subside) {
        Subside originalSubside = subsideRepository.findById(subside.getId()).orElse(Subside.NONE);
        if (originalSubside.equals(Subside.NONE)) {
            throw new IllegalArgumentException("Subside not found");
        }
        if (originalSubside.getCreatedBy().equals(config.username())) {
            if (subside.getName().isBlank()) {
                throw new IllegalArgumentException("Subside name must be specified");
            }
            return subsideRepository.saveAndFlush(subside);
        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }

    @Override
    public void deleteSubsideSafe(Subside subside) {
        Subside originalSubside = subsideRepository.findById(subside.getId()).orElse(Subside.NONE);
        if (originalSubside.getCreatedBy().equals(config.username())) {
            if (cropRepository.findAllBySubsidesContains(subside).isEmpty()) {
                subsideRepository.delete(subside);
                return;
            }
            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }
}
