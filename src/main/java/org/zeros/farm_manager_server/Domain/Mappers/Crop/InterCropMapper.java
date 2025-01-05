package org.zeros.farm_manager_server.Domain.Mappers.Crop;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.zeros.farm_manager_server.Domain.DTO.Crop.InterCropDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.InterCrop;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        typeConversionPolicy = ReportingPolicy.ERROR)
public interface InterCropMapper extends DtoFromEntityMapper<InterCropDTO, InterCrop> {

}
