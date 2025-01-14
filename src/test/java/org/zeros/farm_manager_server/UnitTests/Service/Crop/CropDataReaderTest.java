package org.zeros.farm_manager_server.UnitTests.Service.Crop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfigurationForServiceTest;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSummary.CropSummary;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSummary.ResourcesSummary;
import org.zeros.farm_manager_server.Domain.DTO.Operations.SeedingDTO;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Default.Data.FarmingMachineManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropDataReader;
import org.zeros.farm_manager_server.Services.Interface.Operations.AgriculturalOperationsManager;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan("org.zeros.farm_manager_server.Services")
@Import(LoggedUserConfigurationForServiceTest.class)
public class CropDataReaderTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    Crop unsoldCrop;
    Crop activeCrop;
    Crop archivedCrop;
    @Autowired
    private CropDataReader cropDataReader;
    @Autowired
    private FarmingMachineManagerDefault farmingMachineManager;
    @Autowired
    private AgriculturalOperationsManager agriculturalOperationsManager;

    @BeforeEach
    public void setUp() {
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        loggedUserConfiguration.replaceUser(user);
        Field field = user.getFields().stream().findAny().orElse(Field.NONE);
        FieldPart fieldPart = field.getFieldParts().stream().findAny().orElse(FieldPart.NONE);
        unsoldCrop = fieldPart.getCrops().stream().filter(crop ->
                (crop instanceof MainCrop && !((MainCrop) crop).getIsFullySold() && crop
                        .getWorkFinished())
        ).findAny().orElse(null);
        activeCrop = fieldPart.getActiveCrop();
        archivedCrop = fieldPart.getArchivedCrops().stream().findFirst().orElse(MainCrop.NONE);

    }

    @Test
    void getCropSummary() {
        CropSummary summaryActive = cropDataReader.getCropSummary(activeCrop.getId());
        assertThat(summaryActive).isNotNull();
        assertThat(summaryActive.getCropId()).isNotNull();
        assertThat(summaryActive.getArea().floatValue()).isGreaterThan(0);
        assertThat(summaryActive.getTotalFertilizerCostPerAreaUnit().floatValue()).isGreaterThan(0);
        assertThat(summaryActive.getTotalSprayCostPerAreaUnit().floatValue()).isGreaterThan(0);
        assertThat(summaryActive.getTotalFuelCostPerAreaUnit().floatValue()).isGreaterThan(0);
        assertThat(summaryActive.getMeanSellPrice().size()).isEqualTo(0);

        CropSummary summaryUnsold = cropDataReader.getCropSummary(unsoldCrop.getId());
        assertThat(summaryUnsold).isNotNull();
        assertThat(summaryUnsold.getCropId()).isNotNull();
        assertThat(summaryUnsold.getArea().floatValue()).isGreaterThan(0);
        assertThat(summaryUnsold.getTotalFertilizerCostPerAreaUnit().floatValue()).isGreaterThan(0);
        assertThat(summaryUnsold.getTotalFuelCostPerAreaUnit().floatValue()).isGreaterThan(0);
        if (unsoldCrop instanceof MainCrop) {
            if (!((MainCrop) unsoldCrop).getHarvest().isEmpty()) {
                assertThat(summaryUnsold.getEstimatedAmountNotSoldPerAreaUnit().size()).isEqualTo(1);
                assertThat(summaryUnsold.getEstimatedAmountNotSoldPerAreaUnit()
                        .get(summaryUnsold.getEstimatedAmountNotSoldPerAreaUnit().keySet().stream().findFirst()
                                .get()).floatValue()).isNotEqualTo(0);
            }
        }
        CropSummary summaryArchived = cropDataReader.getCropSummary(archivedCrop.getId());
        assertThat(summaryArchived).isNotNull();
        assertThat(summaryArchived.getCropId()).isNotNull();
        assertThat(summaryArchived.getArea().floatValue()).isGreaterThan(0);
        assertThat(summaryArchived.getTotalFertilizerCostPerAreaUnit().floatValue()).isGreaterThan(0);
        assertThat(summaryArchived.getTotalSprayCostPerAreaUnit().floatValue()).isGreaterThan(0);
        assertThat(summaryArchived.getTotalFuelCostPerAreaUnit().floatValue()).isGreaterThan(0);
        assertThat(summaryArchived.getMeanSellPrice().size()).isEqualTo(1);
        assertThat(summaryArchived.getYieldPerAreaUnit().size()).isEqualTo(1);
        assertThat(summaryArchived.getEstimatedAmountNotSoldPerAreaUnit().size()).isEqualTo(0);
    }

    @Test
    void testGetCropResourcesSummaryArchived() {
        ResourcesSummary summary = cropDataReader.getCropResourcesSummary(archivedCrop.getId());
        assertThat(summary).isNotNull();
        assertThat(summary.getCropId()).isNotNull();
        assertThat(summary.getArea().floatValue()).isGreaterThan(0);
        assertThat(summary.getFertilizerPerAreaUnit().size()).isGreaterThan(0);
        assertThat(summary.getSprayPerAreaUnit().size()).isGreaterThan(0);
        assertThat(summary.getSeedingMaterialPerAreaUnit().size()).isGreaterThan(0);
    }

    @Test
    void testGetCropResourcesSummaryUnsold() {
        ResourcesSummary summary = cropDataReader.getCropResourcesSummary(unsoldCrop.getId());
        assertThat(summary).isNotNull();
        assertThat(summary.getCropId()).isNotNull();
        assertThat(summary.getArea().floatValue()).isGreaterThan(0);
        assertThat(summary.getFertilizerPerAreaUnit().size()).isGreaterThan(0);
        assertThat(summary.getSprayPerAreaUnit().size()).isGreaterThan(0);
        assertThat(summary.getSeedingMaterialPerAreaUnit().size()).isGreaterThan(0);
    }

    @Test
    void testGetPlannedResourcesSummary() {
        Random random = new Random();
        agriculturalOperationsManager.planOperation(activeCrop.getId(),
                SeedingDTO.builder()
                        .sownPlants(Set.copyOf(activeCrop.getCultivatedPlants()).stream().map(BaseEntity::getId).collect(Collectors.toSet()))
                        .farmingMachine(farmingMachineManager.getUndefinedFarmingMachine().getId())
                        .quantityPerAreaUnit(BigDecimal.valueOf(random.nextFloat() * 100))
                        .thousandSeedsMass(BigDecimal.valueOf(random.nextFloat() * 50))
                        .depth(BigDecimal.valueOf(random.nextFloat() * 10))
                        .seedsCostPerUnit(BigDecimal.valueOf(random.nextFloat() * 2000))
                        .fuelConsumptionPerUnit(BigDecimal.valueOf(random.nextFloat() * 30))
                        .fuelPrice(BigDecimal.valueOf(5.17))
                        .build());
        ResourcesSummary summary = cropDataReader.getPlannedResourcesSummary(activeCrop.getId());
        assertThat(summary).isNotNull();
        assertThat(summary.getCropId()).isNotNull();
        assertThat(summary.getArea().floatValue()).isGreaterThan(0);
        assertThat(summary.getSeedingMaterialPerAreaUnit().size()).isGreaterThan(0);
    }

    @Test
    void getMeanCropParameters() {
        Map<ResourceType, CropParametersDTO> parameters = cropDataReader.getMeanCropParameters(archivedCrop.getId());
        assertThat(parameters.get(ResourceType.GRAIN)).isNotNull();
    }

}
