package org.zeros.farm_manager_server.Services.Default;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.HarvestRepository;
import org.zeros.farm_manager_server.Repositories.Crop.CropParametersRepository;
import org.zeros.farm_manager_server.Repositories.Crop.CropSaleRepository;
import org.zeros.farm_manager_server.Services.Interface.CropParametersManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class CropParametersManagerDefault implements CropParametersManager {
    private final CropParametersRepository cropParametersRepository;
    private final LoggedUserConfiguration config;
    private final CropSaleRepository cropSaleRepository;
    private final HarvestRepository harvestRepository;


    @Override
    public CropParameters getCropParametersById(UUID id) {
        return cropParametersRepository.findById(id).orElse(CropParameters.NONE);
    }

    @Override
    public CropParameters createCropParameters(CropParameters cropParameters) {
        if (cropParameters.getName().isBlank() || cropParameters.getId() != null) {
            return CropParameters.NONE;
        }
        cropParameters.setCreatedBy(config.username());
        return cropParametersRepository.saveAndFlush(cropParameters);
    }

    @Override
    public CropParameters updateCropParameters(CropParameters cropParameters) {
        CropParameters originalParameters = getCropParametersById(cropParameters.getId());
        if (originalParameters == CropParameters.NONE) {
            return CropParameters.NONE;
        }
        if (cropParameters.getName().isBlank()) {
            return CropParameters.NONE;
        }
        if (originalParameters.getCreatedBy().equals(config.username())) {
            cropParameters.setCreatedBy(config.username());
            return cropParametersRepository.saveAndFlush(cropParameters);
        }
        throw new IllegalAccessError("Cannot update crop parameters - illegal access");
    }


    @Override
    public Page<CropParameters> getParametersByResourceType(ResourceType resourceType, int pageNumber) {
        return cropParametersRepository.findAllByResourceTypeAndCreatedByIn(
                resourceType,
                config.userRows(),
                PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }

    @Override
    public Page<CropParameters> getParametersByName(String name, int pageNumber) {
        return cropParametersRepository.findAllByNameContainingAndCreatedByIn(
                name,
                config.userRows(),
                PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }

    @Override
    public Page<CropParameters> getAllCropParameters(int pageNumber) {
        return cropParametersRepository.findAllByCreatedByIn(config.userRows(),
                PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name")));
    }


    @Override
    public void deleteCropParametersSafe(CropParameters cropParameters) {
        if (cropParameters.getCreatedBy().equals(config.username())) {
            if (cropSaleRepository.findByCropParameters(cropParameters).isEmpty() &&
                    harvestRepository.findByCropParameters(cropParameters).isEmpty()) {
                throw new IllegalAccessError("Cannot delete crop parameters -usage in other places");
            }
            cropParametersRepository.delete(cropParameters);
        }
        throw new IllegalAccessError("Cannot delete crop parameters - illegal access");
    }

    @Override
    public CropParameters getUndefinedCropParameters() {
        return cropParametersRepository.findAllByNameAndCreatedBy("UNDEFINED", "ADMIN")
                .orElse(CropParameters.NONE);
    }
}
