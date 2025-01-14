package org.zeros.farm_manager_server.UnitTests.Service.User;

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
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.MainCropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Default.User.UserDataReaderDefault;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan("org.zeros.farm_manager_server.Services")
@Import(LoggedUserConfigurationForServiceTest.class)
public class UserDataReaderTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    private UserDataReaderDefault userDataReaderDefault;


    @BeforeEach
    public void setUp() {
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        loggedUserConfiguration.replaceUser(user);
    }


    @Test
    void testGetAllActiveCrops() {
        Set<CropDTO> activeCrops = userDataReaderDefault.getAllActiveCrops();
        assertThat(activeCrops).isNotNull();
        assertThat(activeCrops).hasSize(7);
        for (CropDTO crop : activeCrops) {
            assertThat(crop.getWorkFinished()).isFalse();
            if (crop instanceof MainCropDTO) {
                assertThat(((MainCropDTO) crop).getIsFullySold()).isFalse();
            }
        }
    }

    @Test
    void testGetAllUnsoldCrops() {
        Set<CropDTO> unsold = userDataReaderDefault.getAllUnsoldCrops();
        assertThat(unsold).isNotNull();
        assertThat(unsold.size()).isGreaterThan(0);

        for (CropDTO crop : unsold) {
            assertThat(crop.getWorkFinished()).isTrue();
            assertThat(crop instanceof MainCropDTO).isTrue();
            assertThat(((MainCropDTO) crop).getIsFullySold()).isFalse();

        }
    }

    @Test
    void testGetAllPlannedOperations() {
        Set<AgriculturalOperationDTO> operations = userDataReaderDefault.getAllPlannedOperations(OperationType.ANY);
        assertThat(operations).isNotNull();
        assertThat(operations.size()).isGreaterThan(0);
        for (AgriculturalOperationDTO operation : operations) {
            assertThat(operation.getOperationType()).isNotEqualTo(OperationType.NONE);
            assertThat(operation.getIsPlannedOperation()).isTrue();
        }
    }

    @Test
    void testGetAllPlannedFertilizerApplication() {
        Set<AgriculturalOperationDTO> operations = userDataReaderDefault.getAllPlannedOperations(OperationType.FERTILIZER_APPLICATION);
        assertThat(operations).isNotNull();
        assertThat(operations.size()).isGreaterThan(0);
        for (AgriculturalOperationDTO operation : operations) {
            assertThat(operation.getOperationType()).isEqualTo(OperationType.FERTILIZER_APPLICATION);
            assertThat(operation.getIsPlannedOperation()).isTrue();
        }
    }

}
