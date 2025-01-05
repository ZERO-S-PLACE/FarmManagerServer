package org.zeros.farm_manager_server.UnitTests.Service.Crop;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Bootstrap.DemoUserSetup;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.Operations.SeedingDTO;
import org.zeros.farm_manager_server.Domain.DTO.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.CropSummary.CropSummary;
import org.zeros.farm_manager_server.Domain.DTO.CropSummary.ResourcesSummary;
import org.zeros.farm_manager_server.Domain.Entities.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Services.Default.Crop.CropDataReaderDefault;
import org.zeros.farm_manager_server.Services.Default.Crop.CropManagerDefault;
import org.zeros.farm_manager_server.Services.Default.CropParameters.CropParametersManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Data.*;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldGroupManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldPartManagerDefault;
import org.zeros.farm_manager_server.Services.Default.User.UserDataReaderDefault;
import org.zeros.farm_manager_server.Services.Default.User.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropDataReader;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({DemoUserSetup.class, UserDataReaderDefault.class, CropDataReaderDefault.class, FieldManagerDefault.class,FieldPartManagerDefault.class, FieldGroupManagerDefault.class, PlantManagerDefault.class, SpeciesManagerDefault.class, SprayManagerDefault.class, FertilizerManagerDefault.class, FarmingMachineManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class, CropManagerDefault.class, CropParametersManagerDefault.class, SubsideManagerDefault.class})
public class CropDataReaderTest {

    @Autowired
    FieldPartManager fieldPartManager;
    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    FieldGroupRepository fieldGroupRepositoryRepository;
    @Autowired
    FieldPartRepository fieldPartRepository;
    @Autowired
    UserManager userManager;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    EntityManager entityManager;
    Crop unsoldCrop;
    Crop activeCrop;
    Crop archivedCrop;
    @Autowired
    private CropManager cropManager;
    @Autowired
    private CropDataReader cropDataReader;
    @Autowired
    private UserDataReaderDefault userDataReaderDefault;
    @Autowired
    private FarmingMachineManagerDefault farmingMachineManager;


    @BeforeEach
    public void setUp() {
        User user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
        Field field = user.getFields().stream().findAny().orElse(Field.NONE);
        FieldPart fieldPart = field.getFieldParts().stream().findAny().orElse(FieldPart.NONE);
        unsoldCrop = userDataReaderDefault.getAllUnsoldCrops().stream().findAny().orElse(null);
        activeCrop = fieldPart.getActiveCrop();
        archivedCrop = fieldPart.getArchivedCrops().stream().findAny().orElse(null);

    }

    @Test
    void getCropSummary() {
        CropSummary summaryActive = cropDataReader.getCropSummary(activeCrop.getId());
        assertThat(summaryActive).isNotNull();
        assertThat(summaryActive.getCropId()).isNotNull();
        assertThat(summaryActive.getArea()).isGreaterThan(0);
        assertThat(summaryActive.getTotalFertilizerCostPerAreaUnit()).isGreaterThan(0);
        assertThat(summaryActive.getTotalSprayCostPerAreaUnit()).isGreaterThan(0);
        assertThat(summaryActive.getTotalFuelCostPerAreaUnit()).isGreaterThan(0);
        assertThat(summaryActive.getMeanSellPrice().size()).isEqualTo(0);

        CropSummary summaryUnsold = cropDataReader.getCropSummary(unsoldCrop.getId());
        assertThat(summaryUnsold).isNotNull();
        assertThat(summaryUnsold.getCropId()).isNotNull();
        assertThat(summaryUnsold.getArea()).isGreaterThan(0);
        assertThat(summaryUnsold.getTotalFertilizerCostPerAreaUnit()).isGreaterThan(0);
        assertThat(summaryUnsold.getTotalFuelCostPerAreaUnit()).isGreaterThan(0);
        if (unsoldCrop instanceof MainCrop) {
            if (!((MainCrop) unsoldCrop).getHarvest().isEmpty()) {
                assertThat(summaryUnsold.getEstimatedAmountNotSoldPerAreaUnit().size()).isEqualTo(1);
                assertThat(summaryUnsold.getEstimatedAmountNotSoldPerAreaUnit()
                        .get(summaryUnsold.getEstimatedAmountNotSoldPerAreaUnit().keySet().stream().findFirst()
                                .get()).floatValue()).isGreaterThan(0);
            }
        }
        CropSummary summaryArchived = cropDataReader.getCropSummary(archivedCrop.getId());
        assertThat(summaryArchived).isNotNull();
        assertThat(summaryArchived.getCropId()).isNotNull();
        assertThat(summaryArchived.getArea()).isGreaterThan(0);
        assertThat(summaryArchived.getTotalFertilizerCostPerAreaUnit()).isGreaterThan(0);
        assertThat(summaryArchived.getTotalSprayCostPerAreaUnit()).isGreaterThan(0);
        assertThat(summaryArchived.getTotalFuelCostPerAreaUnit()).isGreaterThan(0);
        assertThat(summaryArchived.getMeanSellPrice().size()).isEqualTo(1);
        assertThat(summaryArchived.getYieldPerAreaUnit().size()).isEqualTo(1);
        assertThat(summaryArchived.getEstimatedAmountNotSoldPerAreaUnit().size()).isEqualTo(0);


    }

    @Test
    void testGetCropResourcesSummaryArchived() {
        ResourcesSummary summary = cropDataReader.getCropResourcesSummary(archivedCrop.getId());
        assertThat(summary).isNotNull();
        assertThat(summary.getCropId()).isNotNull();
        assertThat(summary.getArea()).isGreaterThan(0);
        assertThat(summary.getFertilizerPerAreaUnit().size()).isGreaterThan(0);
        assertThat(summary.getSprayPerAreaUnit().size()).isGreaterThan(0);
        assertThat(summary.getSeedingMaterialPerAreaUnit().size()).isGreaterThan(0);
    }
    @Test
    void testGetCropResourcesSummaryUnsold() {
        ResourcesSummary summary = cropDataReader.getCropResourcesSummary(unsoldCrop.getId());
        assertThat(summary).isNotNull();
        assertThat(summary.getCropId()).isNotNull();
        assertThat(summary.getArea()).isGreaterThan(0);
        assertThat(summary.getFertilizerPerAreaUnit().size()).isGreaterThan(0);
        assertThat(summary.getSprayPerAreaUnit().size()).isGreaterThan(0);
        assertThat(summary.getSeedingMaterialPerAreaUnit().size()).isGreaterThan(0);
    }

    @Test
    void testGetPlannedResourcesSummary() {
        Random random = new Random();
        cropManager.planSeeding(activeCrop.getId(),
                SeedingDTO.builder()
                        .sownPlants(Set.copyOf(activeCrop.getCultivatedPlants()).stream().map(BaseEntity::getId).collect(Collectors.toSet()))
                        .farmingMachine(farmingMachineManager.getUndefinedFarmingMachine().getId())
                        .quantityPerAreaUnit(random.nextFloat() * 100)
                        .thousandSeedsMass(random.nextFloat() * 50)
                        .depth(random.nextFloat() * 10)
                        .seedsCostPerUnit(random.nextFloat() * 2000)
                        .fuelConsumptionPerUnit(random.nextFloat() * 30)
                        .fuelPrice(5.17f)
                        .build());
        ResourcesSummary summary = cropDataReader.getPlannedResourcesSummary(activeCrop.getId());
        assertThat(summary).isNotNull();
        assertThat(summary.getCropId()).isNotNull();
        assertThat(summary.getArea()).isGreaterThan(0);
        assertThat(summary.getSeedingMaterialPerAreaUnit().size()).isGreaterThan(0);
    }

    @Test
    void getMeanCropParameters() {
        Map<ResourceType, CropParametersDTO> parameters = cropDataReader.getMeanCropParameters(archivedCrop.getId());
        assertThat(parameters.get(ResourceType.GRAIN)).isNotNull();
    }

}
