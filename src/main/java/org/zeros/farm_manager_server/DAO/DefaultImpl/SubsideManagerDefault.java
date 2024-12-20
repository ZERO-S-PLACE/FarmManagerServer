package org.zeros.farm_manager_server.DAO.DefaultImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.SubsideManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.entities.Crops.Subside;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.model.ApplicationDefaults;
import org.zeros.farm_manager_server.repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.repositories.Data.SubsideRepository;

import java.rmi.NoSuchObjectException;
import java.util.Set;
import java.util.UUID;
@Component
public class SubsideManagerDefault implements SubsideManager {
    private User user;
    private final SubsideRepository subsideRepository;
    private final CropRepository cropRepository;

    public SubsideManagerDefault(LoggedUserConfiguration loggedUserConfiguration, SubsideRepository subsideRepository, CropRepository cropRepository) {
        this.subsideRepository = subsideRepository;
        this.cropRepository = cropRepository;
        this.user=loggedUserConfiguration.getLoggedUserProperty().get();
        loggedUserConfiguration.getLoggedUserProperty().addListener(((observable, oldValue, newValue) -> user=newValue));
    }

    @Override
    public Page<Subside> getAllSubsides(int pageNumber) {
        return subsideRepository.findAllByCreatedByIn(Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name").descending()));
    }

    @Override
    public Page<Subside> getDefaultSubsides(int pageNumber) {
        return subsideRepository.findAllByCreatedByIn(Set.of("ADMIN"),PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name").descending()));
    }

    @Override
    public Page<Subside> getUserSubsides(int pageNumber) {
        return subsideRepository.findAllByCreatedByIn(Set.of(user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name").descending()));
    }

    @Override
    public Page<Subside> getSubsidesByNameAs(String name,int pageNumber) {
        return subsideRepository.findAllByNameContainingIgnoreCaseAndCreatedByIn(
                name,Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name").descending()));
    }

    @Override
    public Page<Subside> getSubsidesBySpeciesAllowed(Species species,int pageNumber) {
        return subsideRepository.findAllBySpeciesAllowedContainsAndCreatedByIn(
                species,Set.of("ADMIN", user.getUsername()),PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name").descending()));
    }

    @Override
    public Subside getSubsideById(UUID id) {
        return subsideRepository.getSubsideById(id).orElse(Subside.NONE);
    }

    @Override
    public Subside addSubside(Subside subside) {
        if(subside.getName().isBlank()){
            return Subside.NONE;
        }
        if(subsideRepository.findByNameAndCreatedByIn(subside.getName(),Set.of("ADMIN", user.getUsername())).isPresent()){
            return Subside.NONE;
        }
        subside.setCreatedBy(user.getUsername());
        return subsideRepository.saveAndFlush(subside);
    }

    @Override
    public Subside updateSubside(Subside subside){
        Subside originalSubside=subsideRepository.findById(subside.getId()).orElse(Subside.NONE);
        if(originalSubside.equals(Subside.NONE)){
           return Subside.NONE;
        }
        if(originalSubside.getCreatedBy().equals(user.getUsername())){
            if(subside.getName().isBlank()){subside.setName("New Subside");}
            return subsideRepository.saveAndFlush(subside);
        }
        throw new IllegalAccessError("You can't modify this object-no access");

    }

    @Override
    public void deleteSubsideSafe(Subside subside) {
        Subside originalSubside = subsideRepository.findById(subside.getId()).orElse(Subside.NONE);
        if (originalSubside.getCreatedBy().equals(user.getUsername())) {
            if (cropRepository.findAllBySubsidesContains(subside).isEmpty()) {
                subsideRepository.delete(subside);
                return;
            }
            throw new IllegalAccessError("You can't modify this object-usage in other places");
        }
        throw new IllegalAccessError("You can't modify this object-no access");
    }
}
