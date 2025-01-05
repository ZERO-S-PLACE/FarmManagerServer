package org.zeros.farm_manager_server.Services.Default.CropParameters;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Exception.Enum.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.Exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Domain.DTO.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.HarvestRepository;
import org.zeros.farm_manager_server.Repositories.Crop.CropParametersRepository;
import org.zeros.farm_manager_server.Repositories.Crop.CropSaleRepository;
import org.zeros.farm_manager_server.Services.Interface.CropParameters.CropParametersManager;

import java.util.Set;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class CropParametersManagerDefault implements CropParametersManager {
    private final CropParametersRepository cropParametersRepository;
    private final LoggedUserConfiguration config;
    private final CropSaleRepository cropSaleRepository;
    private final HarvestRepository harvestRepository;

    private static PageRequest getPageRequest(int pageNumber) {
        return PageRequest.of(pageNumber, ApplicationDefaults.pageSize, Sort.by("name"));
    }

    @Override
    public Page<CropParameters> getAllCropParameters(int pageNumber) {
        return cropParametersRepository.findAllByCreatedByIn(config.userRows(),
                getPageRequest(pageNumber));
    }

    @Override
    public Page<CropParameters> getParametersByResourceType(ResourceType resourceType, int pageNumber) {
        return cropParametersRepository.findAllByResourceTypeAndCreatedByIn(
                resourceType,
                config.userRows(),
                getPageRequest(pageNumber));
    }

    @Override
    public Page<CropParameters> getParametersByName(String name, int pageNumber) {
        return cropParametersRepository.findAllByNameContainingIgnoreCaseAndCreatedByIn(
                name,
                config.userRows(),
                getPageRequest(pageNumber));
    }

    @Override
    public Page<CropParameters> getParametersByNameAndResourceType(String name, ResourceType resourceType, int pageNumber) {
        return cropParametersRepository.findAllByNameContainingIgnoreCaseAndResourceTypeAndCreatedByIn(
                name,
                resourceType,
                config.userRows(),
                getPageRequest(pageNumber));
    }

    @Override
    public Page<CropParameters> getCropParametersCriteria(String name, ResourceType resourceType, int pageNumber) {
        boolean nameNotPresent = name == null || name.isEmpty();
        boolean resourceTypeNotPresent = resourceType == null || resourceType.equals(ResourceType.ANY);
        if (nameNotPresent && resourceTypeNotPresent) {
            return getAllCropParameters(pageNumber);
        }
        if (resourceTypeNotPresent) {
            return getParametersByName(name, pageNumber);
        }
        if (nameNotPresent) {
            return getParametersByResourceType(resourceType, pageNumber);
        }
        return getParametersByNameAndResourceType(name, resourceType, pageNumber);
    }

    @Override
    public CropParameters getCropParametersById(UUID id) {
        return cropParametersRepository.findById(id).orElse(CropParameters.NONE);
    }

    @Override
    public CropParameters addCropParameters(CropParametersDTO cropParametersDTO) {
        checkIfRequiredFieldsPresent(cropParametersDTO);
        checkIfUniqueObject(cropParametersDTO);
        return cropParametersRepository.saveAndFlush(rewriteToEntity(cropParametersDTO, CropParameters.NONE));
    }

    private void checkIfRequiredFieldsPresent(CropParametersDTO cropParametersDTO) {
        if (cropParametersDTO.getName() == null || cropParametersDTO.getName().isBlank()) {
            throw new IllegalArgumentExceptionCustom(
                    CropParameters.class,
                    Set.of("name"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
    }

    private void checkIfUniqueObject(CropParametersDTO cropParametersDTO) {
        if (cropParametersDTO.getId() == null && cropParametersRepository.findAllByNameAndCreatedByIn(
                cropParametersDTO.getName(),
                config.allRows(), getPageRequest(0)).isEmpty()) {
            return;
        }
        throw new IllegalArgumentExceptionCustom(
                CropParameters.class,
                IllegalArgumentExceptionCause.OBJECT_EXISTS);
    }

    private CropParameters rewriteToEntity(CropParametersDTO dto, CropParameters entity) {
        CropParameters entityParsed = DefaultMappers.cropParametersMapper.dtoToEntitySimpleProperties(dto);
        entityParsed.setCreatedBy(config.username());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        return entityParsed;
    }

    @Override
    public CropParameters updateCropParameters(CropParametersDTO cropParametersDTO) {
        CropParameters originalParameters = getCropParametersIfExist(cropParametersDTO.getId());
        checkAccess(originalParameters);
        checkIfRequiredFieldsPresent(cropParametersDTO);
        return cropParametersRepository.saveAndFlush(rewriteToEntity(cropParametersDTO, originalParameters));
    }

    private void checkAccess(CropParameters cropParameters) {
        if (cropParameters.getCreatedBy().equals(config.username())) {
            return;
        }
        throw new IllegalAccessErrorCustom(CropParameters.class,
                IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
    }
@Override
    public CropParameters getCropParametersIfExist(UUID cropParametersId) {
        if (cropParametersId == null) {
            throw new IllegalArgumentExceptionCustom(
                    CropParameters.class,
                    Set.of("Id"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        CropParameters originalParameters = getCropParametersById(cropParametersId);
        if (originalParameters.equals(CropParameters.NONE)) {
            throw new IllegalArgumentExceptionCustom(CropParameters.class,
                    IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return originalParameters;
    }


    @Override
    public void deleteCropParametersSafe(UUID cropParametersId) {
        CropParameters cropParameters = getCropParametersById(cropParametersId);
        if (cropParameters == CropParameters.NONE) {
            return;
        }
        checkAccess(cropParameters);
        checkUsages(cropParameters);
        cropParametersRepository.delete(cropParameters);


    }

    private void checkUsages(CropParameters cropParameters) {
        if (cropSaleRepository.findByCropParameters(cropParameters).isEmpty() &&
                harvestRepository.findByCropParameters(cropParameters).isEmpty()) {
            return;
        }
        throw new IllegalAccessErrorCustom(CropParameters.class,
                IllegalAccessErrorCause.USAGE_IN_OTHER_PLACES);
    }

    @Override
    public CropParameters getUndefinedCropParameters() {
        return cropParametersRepository.findAllByNameAndCreatedBy("UNDEFINED", "ADMIN")
                .orElse(CropParameters.NONE);
    }

}
