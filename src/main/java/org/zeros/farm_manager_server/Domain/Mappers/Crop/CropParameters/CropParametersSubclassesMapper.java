package org.zeros.farm_manager_server.Domain.Mappers.Crop.CropParameters;


import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.GrainParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.RapeSeedParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.SugarBeetParametersDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.GrainParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.RapeSeedParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.SugarBeetParameters;
import org.zeros.farm_manager_server.Domain.Mappers.DtoFromEntityMapper;

@RequiredArgsConstructor
public class CropParametersSubclassesMapper implements DtoFromEntityMapper<CropParametersDTO, CropParameters> {
    public static final CropParametersMapper cropParametersMapper = Mappers.getMapper(CropParametersMapper.class);
    public static final GrainParametersMapper grainParametersMapper = Mappers.getMapper(GrainParametersMapper.class);
    public static final RapeSeedParametersMapper rapeSeedParametersMapper = Mappers.getMapper(RapeSeedParametersMapper.class);
    public static final SugarBeetParametersMapper sugarBeetParametersMapper = Mappers.getMapper(SugarBeetParametersMapper.class);


    @Override
    public CropParametersDTO entityToDto(CropParameters entity) {
        return switch (entity) {
            case GrainParameters grainParameters -> grainParametersMapper.entityToDto(grainParameters);
            case RapeSeedParameters rapeSeedParameters -> rapeSeedParametersMapper.entityToDto(rapeSeedParameters);
            case SugarBeetParameters sugarBeetParameters -> sugarBeetParametersMapper.entityToDto(sugarBeetParameters);
            default -> cropParametersMapper.entityToDto(entity);
        };
    }

    @Override
    public CropParameters dtoToEntitySimpleProperties(CropParametersDTO dto) {
        return switch (dto) {
            case GrainParametersDTO grainParametersDTO ->
                    grainParametersMapper.dtoToEntitySimpleProperties(grainParametersDTO);
            case RapeSeedParametersDTO rapeSeedParametersDTO ->
                    rapeSeedParametersMapper.dtoToEntitySimpleProperties(rapeSeedParametersDTO);
            case SugarBeetParametersDTO sugarBeetParametersDTO ->
                    sugarBeetParametersMapper.dtoToEntitySimpleProperties(sugarBeetParametersDTO);
            default -> cropParametersMapper.dtoToEntitySimpleProperties(dto);
        };
    }
}
