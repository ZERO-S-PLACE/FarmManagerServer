package org.zeros.farm_manager_server.Domain.Mappers;

import org.mapstruct.factory.Mappers;
import org.zeros.farm_manager_server.Domain.Mappers.AgriculturalOperations.Data.FarmingMachineMapper;
import org.zeros.farm_manager_server.Domain.Mappers.AgriculturalOperations.Data.FertilizerMapper;
import org.zeros.farm_manager_server.Domain.Mappers.AgriculturalOperations.Data.SprayMapper;
import org.zeros.farm_manager_server.Domain.Mappers.AgriculturalOperations.Operations.*;
import org.zeros.farm_manager_server.Domain.Mappers.Crop.Crop.CropSubclassesMapper;
import org.zeros.farm_manager_server.Domain.Mappers.Crop.Crop.InterCropMapper;
import org.zeros.farm_manager_server.Domain.Mappers.Crop.Crop.MainCropMapper;
import org.zeros.farm_manager_server.Domain.Mappers.Crop.CropParameters.*;
import org.zeros.farm_manager_server.Domain.Mappers.Crop.CropSaleMapper;
import org.zeros.farm_manager_server.Domain.Mappers.Crop.Plant.PlantMapper;
import org.zeros.farm_manager_server.Domain.Mappers.Crop.Plant.SpeciesMapper;
import org.zeros.farm_manager_server.Domain.Mappers.Crop.SubsideMapper;
import org.zeros.farm_manager_server.Domain.Mappers.Fields.FieldGroupMapper;
import org.zeros.farm_manager_server.Domain.Mappers.Fields.FieldMapper;
import org.zeros.farm_manager_server.Domain.Mappers.Fields.FieldPartMapper;
import org.zeros.farm_manager_server.Domain.Mappers.User.UserMapper;

public class DefaultMappers {
    public static final FarmingMachineMapper farmingMachineMapper = Mappers.getMapper(FarmingMachineMapper.class);
    public static final FertilizerMapper fertilizerMapper = Mappers.getMapper(FertilizerMapper.class);
    public static final SprayMapper sprayMapper = Mappers.getMapper(SprayMapper.class);
    public static final AgriculturalOperationSubclassesMapper agriculturalOperationMapper = new AgriculturalOperationSubclassesMapper();
    public static final CultivationMapper cultivationMapper = Mappers.getMapper(CultivationMapper.class);
    public static final FertilizerApplicationMapper fertilizerApplicationMapper = Mappers.getMapper(FertilizerApplicationMapper.class);
    public static final HarvestMapper harvestMapper = Mappers.getMapper(HarvestMapper.class);
    public static final SeedingMapper seedingMapper = Mappers.getMapper(SeedingMapper.class);
    public static final SprayApplicationMapper sprayApplicationMapper = Mappers.getMapper(SprayApplicationMapper.class);
    public static final InterCropMapper interCropMapper = Mappers.getMapper(InterCropMapper.class);
    public static final MainCropMapper mainCropMapper = Mappers.getMapper(MainCropMapper.class);
    public static final CropSubclassesMapper cropMapper = new CropSubclassesMapper();
    public static final CropParametersSubclassesMapper cropParametersMapper = new CropParametersSubclassesMapper();
    public static final PlantMapper plantMapper = Mappers.getMapper(PlantMapper.class);
    public static final SpeciesMapper speciesMapper = Mappers.getMapper(SpeciesMapper.class);
    public static final CropSaleMapper cropSaleMapper = Mappers.getMapper(CropSaleMapper.class);
    public static final SubsideMapper subsideMapper = Mappers.getMapper(SubsideMapper.class);
    public static final FieldGroupMapper fieldGroupMapper = Mappers.getMapper(FieldGroupMapper.class);
    public static final FieldMapper fieldMapper = Mappers.getMapper(FieldMapper.class);
    public static final FieldPartMapper fieldPartMapper = Mappers.getMapper(FieldPartMapper.class);
    public static final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
}
