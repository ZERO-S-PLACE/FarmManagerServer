package org.zeros.farm_manager_server.UnitTests.Service.User;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.Entities.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Entities.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Services.Default.Crop.CropManagerDefault;
import org.zeros.farm_manager_server.Services.Default.CropParameters.CropParametersManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Data.*;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldGroupManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldManagerDefault;
import org.zeros.farm_manager_server.Services.Default.Fields.FieldPartManagerDefault;
import org.zeros.farm_manager_server.Services.Default.User.UserDataReaderDefault;
import org.zeros.farm_manager_server.Services.Default.User.UserManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserDataReaderDefault.class, FieldManagerDefault.class,FieldPartManagerDefault.class, FieldGroupManagerDefault.class, PlantManagerDefault.class, SpeciesManagerDefault.class, SprayManagerDefault.class, FertilizerManagerDefault.class, FarmingMachineManagerDefault.class, UserManagerDefault.class, LoggedUserConfiguration.class, CropManagerDefault.class, CropParametersManagerDefault.class, SubsideManagerDefault.class})
public class UserDataReaderTest {

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
    @Autowired
    private UserDataReaderDefault userDataReaderDefault;




    @BeforeEach
    public void setUp() {
         userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
    }


    @Test
    void testGetAllActiveCrops() {
        Set<Crop> activeCrops = userDataReaderDefault.getAllActiveCrops();
        assertThat(activeCrops).isNotNull();
        assertThat(activeCrops).hasSize(7);
        Set<FieldPart> fieldParts = new HashSet<>();
        for (Crop crop : activeCrops) {
            assertThat(crop.getWorkFinished()).isFalse();
            if (crop instanceof MainCrop) {
                assertThat(((MainCrop) crop).getIsFullySold()).isFalse();
            }
            assertThat(fieldParts.contains(crop.getFieldPart())).isFalse();
            fieldParts.add(crop.getFieldPart());
        }
    }

    @Test
    void testGetAllUnsoldCrops() {
        Set<Crop> unsold = userDataReaderDefault.getAllUnsoldCrops();
        assertThat(unsold).isNotNull();
        assertThat(unsold.size()).isGreaterThan(0);

        for (Crop crop : unsold) {
            assertThat(crop.getWorkFinished()).isTrue();
            if (crop instanceof MainCrop) {
                assertThat(((MainCrop) crop).getIsFullySold()).isFalse();
            }
        }
    }

    @Test
    void testGetAllPlannedOperations() {
        Set<AgriculturalOperation> operations = userDataReaderDefault.getAllPlannedOperations(OperationType.ANY);
        assertThat(operations).isNotNull();
        assertThat(operations.size()).isGreaterThan(0);
        for (AgriculturalOperation operation : operations) {
            assertThat(operation.getOperationType()).isNotEqualTo(OperationType.NONE);
            assertThat(operation.getIsPlannedOperation()).isTrue();
        }
    }

    @Test
    void testGetAllPlannedFertilizerApplication() {
        Set<AgriculturalOperation> operations = userDataReaderDefault.getAllPlannedOperations(OperationType.FERTILIZER_APPLICATION);
        assertThat(operations).isNotNull();
        assertThat(operations.size()).isGreaterThan(0);
        for (AgriculturalOperation operation : operations) {
            assertThat(operation.getOperationType()).isEqualTo(OperationType.FERTILIZER_APPLICATION);
            assertThat(operation.getIsPlannedOperation()).isTrue();
        }
    }

}