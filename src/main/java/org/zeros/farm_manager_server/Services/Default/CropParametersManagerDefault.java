package org.zeros.farm_manager_server.Services.Default;

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
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.GrainParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.RapeSeedParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.SugarBeetParametersDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.AgriculturalOperation.HarvestRepository;
import org.zeros.farm_manager_server.Repositories.Crop.CropParametersRepository;
import org.zeros.farm_manager_server.Repositories.Crop.CropSaleRepository;
import org.zeros.farm_manager_server.Services.Interface.CropParametersManager;

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
        return cropParametersRepository.findAllByNameContainingAndCreatedByIn(
                name,
                config.userRows(),
                getPageRequest(pageNumber));
    }

    @Override
    public CropParameters getCropParametersById(UUID id) {
        return cropParametersRepository.findById(id).orElse(CropParameters.NONE);
    }

    @Override
    public CropParameters createCropParameters(CropParametersDTO cropParametersDTO) {
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
        CropParameters entityParsed;
        if (dto instanceof GrainParametersDTO) {
            entityParsed = DefaultMappers.grainParametersMapper.dtoToEntitySimpleProperties((GrainParametersDTO) dto);
        } else if (dto instanceof RapeSeedParametersDTO) {
            entityParsed = DefaultMappers.rapeSeedParametersMapper.dtoToEntitySimpleProperties((RapeSeedParametersDTO) dto);
        } else if (dto instanceof SugarBeetParametersDTO) {
            entityParsed = DefaultMappers.sugarBeetParametersMapper.dtoToEntitySimpleProperties((SugarBeetParametersDTO) dto);
        } else {
            entityParsed = DefaultMappers.cropParametersMapper.dtoToEntitySimpleProperties(dto);
        }
        entityParsed.setCreatedBy(config.username());
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        return entityParsed;
    }

    @Override
    public CropParameters updateCropParameters(CropParametersDTO cropParametersDTO) {
        CropParameters originalParameters = getCropParametersIfExists(cropParametersDTO);
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

    private CropParameters getCropParametersIfExists(CropParametersDTO cropParametersDTO) {
        if (cropParametersDTO.getId() == null) {
            throw new IllegalArgumentExceptionCustom(
                    CropParameters.class,
                    Set.of("Id"),
                    IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        CropParameters originalParameters = getCropParametersById(cropParametersDTO.getId());
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
