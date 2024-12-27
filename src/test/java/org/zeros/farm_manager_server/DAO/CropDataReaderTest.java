package org.zeros.farm_manager_server.DAO;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.DAO.Default.*;
import org.zeros.farm_manager_server.DAO.Default.Data.*;
import org.zeros.farm_manager_server.DAO.Interface.CropDataReader;
import org.zeros.farm_manager_server.DAO.Interface.CropOperationsManager;
import org.zeros.farm_manager_server.DAO.Interface.UserFieldsManager;
import org.zeros.farm_manager_server.DAO.Interface.UserManager;
import org.zeros.farm_manager_server.bootstrap.DemoUserSetup;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Operations.Seeding;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.entities.Crop.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crop.Crop.MainCrop;
import org.zeros.farm_manager_server.entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.entities.DataTransfer.CropSummary;
import org.zeros.farm_manager_server.entities.DataTransfer.ResourcesSummary;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.Fields.Field;
import org.zeros.farm_manager_server.entities.Fields.FieldPart;
import org.zeros.farm_manager_server.repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.repositories.Fields.FieldRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({DemoUserSetup.class, UserDataReaderDefault.class, CropDataReaderDefault.class, UserFieldsManagerDefault.class, PlantManagerDefault.class, SpeciesManagerDefault.class, SprayManagerDefault.class, FertilizerManagerDefault.class, FarmingMachineManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class, CropOperationsManagerDefault.class, CropParametersManagerDefault.class, SubsideManagerDefault.class})
public class CropDataReaderTest {

    @Autowired
    UserFieldsManager userFieldsManager;
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
    private CropOperationsManager cropOperationsManager;
    @Autowired
    private CropDataReader cropDataReader;
    @Autowired
    private UserDataReaderDefault userDataReaderDefault;


    @BeforeEach
    public void setUp() {
        User user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");

        Field field = user.getFields().stream().findAny().orElse(Field.NONE);
        FieldPart fieldPart = field.getFieldParts().stream().findAny().orElse(FieldPart.NONE);
        unsoldCrop = userDataReaderDefault.getAllUnsoldCrops().stream().findAny().orElse(null);
        activeCrop = fieldPart.getActiveCrop();
        archivedCrop = fieldPart.getArchivedCrops().stream().findFirst().orElse(MainCrop.NONE);

    }

    @Test
    void getCropSummary() {
        CropSummary summaryActive = cropDataReader.getCropSummary(activeCrop);
        assertThat(summaryActive).isNotNull();
        assertThat(summaryActive.getCropId()).isNotNull();
        assertThat(summaryActive.getArea().doubleValue()).isGreaterThan(0);
        assertThat(summaryActive.getTotalFertilizerCostPerAreaUnit().doubleValue()).isGreaterThan(0);
        assertThat(summaryActive.getTotalSprayCostPerAreaUnit().doubleValue()).isGreaterThan(0);
        assertThat(summaryActive.getTotalFuelCostPerAreaUnit().doubleValue()).isGreaterThan(0);
        assertThat(summaryActive.getMeanSellPrice().size()).isEqualTo(0);

        CropSummary summaryUnsold = cropDataReader.getCropSummary(unsoldCrop);
        assertThat(summaryUnsold).isNotNull();
        assertThat(summaryUnsold.getCropId()).isNotNull();
        assertThat(summaryUnsold.getArea().doubleValue()).isGreaterThan(0);
        assertThat(summaryUnsold.getTotalFertilizerCostPerAreaUnit().doubleValue()).isGreaterThan(0);
        assertThat(summaryUnsold.getTotalFuelCostPerAreaUnit().doubleValue()).isGreaterThan(0);
        if (unsoldCrop instanceof MainCrop) {
            if (!((MainCrop) unsoldCrop).getHarvest().isEmpty()) {
                assertThat(summaryUnsold.getEstimatedAmountNotSoldPerAreaUnit().size()).isEqualTo(1);
                assertThat(summaryUnsold.getEstimatedAmountNotSoldPerAreaUnit().get(summaryUnsold.getEstimatedAmountNotSoldPerAreaUnit().keySet().stream().findFirst().orElse(null)).doubleValue()).isGreaterThan(0);
            }
        }
        CropSummary summaryArchived = cropDataReader.getCropSummary(archivedCrop);
        assertThat(summaryArchived).isNotNull();
        assertThat(summaryArchived.getCropId()).isNotNull();
        assertThat(summaryArchived.getArea().doubleValue()).isGreaterThan(0);
        assertThat(summaryArchived.getTotalFertilizerCostPerAreaUnit().doubleValue()).isGreaterThan(0);
        assertThat(summaryArchived.getTotalSprayCostPerAreaUnit().doubleValue()).isGreaterThan(0);
        assertThat(summaryArchived.getTotalFuelCostPerAreaUnit().doubleValue()).isGreaterThan(0);
        assertThat(summaryArchived.getMeanSellPrice().size()).isEqualTo(1);
        assertThat(summaryArchived.getYieldPerAreaUnit().size()).isEqualTo(1);
        assertThat(summaryArchived.getEstimatedAmountNotSoldPerAreaUnit().size()).isEqualTo(0);


    }

    @Test
    void testGetCropResourcesSummary() {
        ResourcesSummary summary = cropDataReader.getCropResourcesSummary(archivedCrop);
        assertThat(summary).isNotNull();
        assertThat(summary.getCropId()).isNotNull();
        assertThat(summary.getArea().doubleValue()).isGreaterThan(0);
        assertThat(summary.getFertilizerPerAreaUnit().size()).isGreaterThan(0);
        assertThat(summary.getSprayPerAreaUnit().size()).isGreaterThan(0);
        assertThat(summary.getSeedingMaterialPerAreaUnit().size()).isGreaterThan(0);

    }

    @Test
    void testGetPlannedResourcesSummary() {
        Random random = new Random();
        cropOperationsManager.planSeeding(activeCrop,
                Seeding.builder()
                        .sownPlants(Set.copyOf(activeCrop.getCultivatedPlants()))
                        .farmingMachine(FarmingMachine.UNDEFINED)
                        .quantityPerAreaUnit(BigDecimal.valueOf(random.nextDouble() * 100))
                        .thousandSeedsMass(BigDecimal.valueOf(random.nextDouble() * 50))
                        .depth(BigDecimal.valueOf(random.nextDouble() * 10))
                        .seedsCostPerUnit(BigDecimal.valueOf(random.nextDouble() * 2000))
                        .fuelConsumptionPerUnit(BigDecimal.valueOf(random.nextDouble() * 30))
                        .fuelPrice(BigDecimal.valueOf(5.17))
                .build());
        ResourcesSummary summary = cropDataReader.getPlannedResourcesSummary(activeCrop);
        assertThat(summary).isNotNull();
        assertThat(summary.getCropId()).isNotNull();
        assertThat(summary.getArea().doubleValue()).isGreaterThan(0);
        assertThat(summary.getSeedingMaterialPerAreaUnit().size()).isGreaterThan(0);
    }

    @Test
    void getMeanCropParameters() {
        Map<ResourceType, CropParameters> parameters = cropDataReader.getMeanCropParameters(archivedCrop);
        assertThat(parameters.get(ResourceType.GRAIN)).isNotNull();
    }

}
