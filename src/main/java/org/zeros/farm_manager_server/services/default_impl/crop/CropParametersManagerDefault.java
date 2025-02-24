package org.zeros.farm_manager_server.services.default_impl.crop;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeros.farm_manager_server.configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.domain.dto.crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.domain.entities.crop.crop_parameters.CropParameters;
import org.zeros.farm_manager_server.domain.enums.ResourceType;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.exception.Enum.IllegalAccessErrorCause;
import org.zeros.farm_manager_server.exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.exception.IllegalAccessErrorCustom;
import org.zeros.farm_manager_server.exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.model.ApplicationDefaults;
import org.zeros.farm_manager_server.repositories.operations.HarvestRepository;
import org.zeros.farm_manager_server.repositories.crop.crop_parameters.CropParametersRepository;
import org.zeros.farm_manager_server.repositories.crop.CropSaleRepository;
import org.zeros.farm_manager_server.services.interfaces.crop.CropParametersManager;

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
    @Transactional(readOnly = true)
    public Page<CropParametersDTO> getAllCropParameters(int pageNumber) {
        return cropParametersRepository.findAllByCreatedByIn(config.userRows(),
                        getPageRequest(pageNumber))
                .map(DefaultMappers.cropParametersMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CropParametersDTO> getParametersByResourceType(ResourceType resourceType, int pageNumber) {
        return cropParametersRepository.findAllByResourceTypeAndCreatedByIn(resourceType,
                        config.userRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.cropParametersMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CropParametersDTO> getParametersByName(String name, int pageNumber) {
        return cropParametersRepository.findAllByNameContainingIgnoreCaseAndCreatedByIn(name,
                        config.userRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.cropParametersMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CropParametersDTO> getParametersByNameAndResourceType(String name, ResourceType resourceType, int pageNumber) {
        return cropParametersRepository.findAllByNameContainingIgnoreCaseAndResourceTypeAndCreatedByIn(
                        name, resourceType,
                        config.userRows(), getPageRequest(pageNumber))
                .map(DefaultMappers.cropParametersMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CropParametersDTO> getCropParametersCriteria(String name, ResourceType resourceType, int pageNumber) {
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
    @Transactional(readOnly = true)
    public CropParametersDTO getCropParametersById(UUID id) {
        CropParameters cropParameters = cropParametersRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(CropParameters.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
        return DefaultMappers.cropParametersMapper.entityToDto(cropParameters);

    }

    @Override
    @Transactional
    public CropParametersDTO addCropParameters(CropParametersDTO cropParametersDTO) {
        checkIfRequiredFieldsPresent(cropParametersDTO);
        checkIfUniqueObject(cropParametersDTO);
        CropParameters saved = cropParametersRepository.saveAndFlush(rewriteToEntity(cropParametersDTO, CropParameters.NONE));
        return DefaultMappers.cropParametersMapper.entityToDto(saved);
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
    @Transactional
    public CropParametersDTO updateCropParameters(CropParametersDTO cropParametersDTO) {
        CropParameters originalParameters = getCropParametersIfExist(cropParametersDTO.getId());
        checkAccess(originalParameters);
        checkIfRequiredFieldsPresent(cropParametersDTO);
        CropParameters updated = cropParametersRepository.saveAndFlush(rewriteToEntity(cropParametersDTO, originalParameters));
        return DefaultMappers.cropParametersMapper.entityToDto(updated);
    }

    private void checkAccess(CropParameters cropParameters) {
        if (cropParameters.getCreatedBy().equals(config.username())) {
            return;
        }
        throw new IllegalAccessErrorCustom(CropParameters.class,
                IllegalAccessErrorCause.UNMODIFIABLE_OBJECT);
    }

    @Override
    @Transactional
    public CropParameters getCropParametersIfExist(UUID cropParametersId) {
        if (cropParametersId == null) {
            throw new IllegalArgumentExceptionCustom(
                    CropParameters.class, Set.of("Id"), IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        return cropParametersRepository.findById(cropParametersId).orElseThrow(() ->
                new IllegalArgumentExceptionCustom(CropParameters.class,
                        IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));

    }


    @Override
    @Transactional
    public void deleteCropParametersSafe(UUID cropParametersId) {
        CropParameters cropParameters = cropParametersRepository.findById(cropParametersId).orElse(CropParameters.NONE);
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
    @Transactional
    public CropParameters getUndefinedCropParameters() {
        return cropParametersRepository.findAllByNameAndCreatedBy("UNDEFINED", "ADMIN")
                .orElse(CropParameters.NONE);
    }

}
