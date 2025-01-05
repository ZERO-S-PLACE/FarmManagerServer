package org.zeros.farm_manager_server.Domain.Mappers.CropParameters;


import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.zeros.farm_manager_server.Domain.DTO.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.CropParameters.GrainParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.CropParameters.RapeSeedParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.CropParameters.SugarBeetParametersDTO;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.GrainParameters;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.RapeSeedParameters;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.SugarBeetParameters;
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
