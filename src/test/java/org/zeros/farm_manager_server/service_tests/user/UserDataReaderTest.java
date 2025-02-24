package org.zeros.farm_manager_server.service_tests.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.zeros.farm_manager_server.configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.configuration.LoggedUserConfigurationForServiceTest;
import org.zeros.farm_manager_server.domain.dto.crop.CropDTO;
import org.zeros.farm_manager_server.domain.dto.crop.MainCropDTO;
import org.zeros.farm_manager_server.domain.dto.operations.AgriculturalOperationDTO;
import org.zeros.farm_manager_server.domain.entities.user.User;
import org.zeros.farm_manager_server.domain.enums.OperationType;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.repositories.user.UserRepository;
import org.zeros.farm_manager_server.services.default_impl.user.UserDataReaderDefault;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan("org.zeros.farm_manager_server.services")
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
