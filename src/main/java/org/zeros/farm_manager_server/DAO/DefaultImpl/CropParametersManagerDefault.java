package org.zeros.farm_manager_server.DAO.DefaultImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.CropParametersManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.ResourceType;
import org.zeros.farm_manager_server.entities.Crops.CropParameters.CropParameters;
import org.zeros.farm_manager_server.model.ApplicationDefaults;
import org.zeros.farm_manager_server.repositories.AgriculturalOperation.HarvestRepository;
import org.zeros.farm_manager_server.repositories.Crop.CropParametersRepository;
import org.zeros.farm_manager_server.repositories.Crop.CropSaleRepository;

import java.util.UUID;
@Component
public class CropParametersManagerDefault implements CropParametersManager {
    private final CropParametersRepository cropParametersRepository;
    private final LoggedUserConfiguration loggedUserConfiguration;
    private final CropSaleRepository cropSaleRepository;
    private final HarvestRepository harvestRepository;

    public CropParametersManagerDefault(CropParametersRepository cropParametersRepository, LoggedUserConfiguration loggedUserConfiguration, CropSaleRepository cropSaleRepository, HarvestRepository harvestRepository) {

        this.cropParametersRepository = cropParametersRepository;
        this.loggedUserConfiguration = loggedUserConfiguration;
        this.cropSaleRepository = cropSaleRepository;
        this.harvestRepository = harvestRepository;
    }

    @Override
    public CropParameters getCropParametersById(UUID id) {
        return cropParametersRepository.findById(id).orElse(CropParameters.NONE);
    }

    @Override
    public CropParameters createCropParameters(CropParameters cropParameters) {
        if(cropParameters.getName().isBlank()||cropParameters.getId()!=null){
            return CropParameters.NONE;
        }
        cropParameters.setCreatedBy(loggedUserConfiguration.username());
        return cropParametersRepository.saveAndFlush(cropParameters);
    }

    @Override
    public CropParameters updateCropParameters(CropParameters cropParameters) {
        CropParameters originalParameters = getCropParametersById(cropParameters.getId());
        if(originalParameters==CropParameters.NONE){
            return CropParameters.NONE;
        }
        if(cropParameters.getName().isBlank()){
            return CropParameters.NONE;
        }
        if(originalParameters.getCreatedBy().equals(loggedUserConfiguration.username())){
        cropParameters.setCreatedBy(loggedUserConfiguration.username());
        return cropParametersRepository.saveAndFlush(cropParameters);
        }
        throw new IllegalAccessError("Cannot update crop parameters - illegal access");
    }


    @Override
    public Page<CropParameters> getParametersByResourceType(ResourceType resourceType, int pageNumber) {
        return cropParametersRepository.findAllByResourceTypeAndCreatedByIn(resourceType,loggedUserConfiguration.userRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }
    @Override
    public Page<CropParameters> getParametersByName(String name, int pageNumber) {
        return cropParametersRepository.findAllByNameContainingAndCreatedByIn(name,loggedUserConfiguration.userRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }

    @Override
    public Page<CropParameters> getAllCropParameters(int pageNumber) {
        return cropParametersRepository.findAllByCreatedByIn(loggedUserConfiguration.userRows(), PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }



    @Override
    public void deleteCropParametersSafe(CropParameters cropParameters) {
        if(cropParameters.getCreatedBy().equals(loggedUserConfiguration.username())){
            if(cropSaleRepository.findByCropParameters(cropParameters).isEmpty()&&harvestRepository.findByCropParameters(cropParameters).isEmpty()){
                throw new IllegalAccessError("Cannot delete crop parameters -usage in other places");
            }
            cropParametersRepository.delete(cropParameters);
        }
        throw new IllegalAccessError("Cannot delete crop parameters -illegal access");
    }

    @Override
    public CropParameters getUndefinedCropParameters() {
        return cropParametersRepository.findAllByNameAndCreatedBy("UNDEFINED","ADMIN").orElse(CropParameters.NONE);
    }
}
