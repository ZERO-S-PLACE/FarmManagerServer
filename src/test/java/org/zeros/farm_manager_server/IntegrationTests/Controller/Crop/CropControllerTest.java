package org.zeros.farm_manager_server.IntegrationTests.Controller.Crop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.zeros.farm_manager_server.Controllers.Crop.CropController;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.InterCropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.MainCropDTO;
import org.zeros.farm_manager_server.Domain.DTO.Data.PlantDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Data.PlantManager;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@Rollback

public class CropControllerTest {

    @Autowired
    CropManager cropManager;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;
    MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlantManager plantManager;

    private FieldPart fieldPart;
    private Crop activeCrop;
    private Crop archivedCrop;
    private PlantDTO plantDTO;


    //ToDo add remaining tests
    //ToDo add tests with invalid input for all classes
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        Field field = user.getFields().stream().findAny().orElseThrow();
        fieldPart = field.getFieldParts().stream().filter(fieldPart1 -> !fieldPart1.getIsArchived()).findAny().orElse(FieldPart.NONE);
        activeCrop = fieldPart.getActiveCrop();
        archivedCrop = fieldPart.getArchivedCrops().stream().findFirst().orElse(MainCrop.NONE);
        plantDTO = plantManager.getPlantById(activeCrop.getCultivatedPlants().stream().findFirst().orElseThrow().getId());
    }

    @Test
    void getById() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(CropController.ID_PATH, activeCrop.getId())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(activeCrop.getId().toString())))
                .andReturn();

        displayResponse(result);
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get(CropController.ID_PATH, UUID.randomUUID().toString())
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addNewMainCrop() throws Exception {
        MvcResult result = mockMvc.perform(post(CropController.MAIN_CROP_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fieldPartId", fieldPart.getId().toString())
                        .param("cultivatedPlantsIds", plantDTO.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        CropDTO crop = cropManager.getCropById(savedUUID);
        assertThat(crop instanceof MainCropDTO).isTrue();
        assertThat(crop.getFieldPart()).isEqualTo(fieldPart.getId());
        assertThat(crop.getWorkFinished()).isEqualTo(false);
        displayResponse(result);
    }

    @Test
    void addNewInterCrop() throws Exception {
        MvcResult result = mockMvc.perform(post(CropController.INTER_CROP_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fieldPartId", fieldPart.getId().toString())
                        .param("cultivatedPlantsIds", plantDTO.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        CropDTO crop = cropManager.getCropById(savedUUID);
        assertThat(crop instanceof InterCropDTO).isTrue();
        assertThat(crop.getFieldPart()).isEqualTo(fieldPart.getId());
        assertThat(crop.getWorkFinished()).isEqualTo(false);
        displayResponse(result);
    }

    @Test
    void updatePlants() throws Exception {
        UUID newPlantId = archivedCrop.getCultivatedPlants()
                .stream().findFirst().orElseThrow().getId();

        mockMvc.perform(patch(CropController.CROP_PLANTS_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)

                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cropId", activeCrop.getId().toString())
                        .param("cultivatedPlantsIds", newPlantId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        CropDTO cropUpdated = cropManager.getCropById(activeCrop.getId());
        assertThat(cropUpdated).isNotNull();
        assertThat(cropUpdated.getCultivatedPlants()).isEqualTo(Set.of(newPlantId));

    }

    @Test
    void deleteCrop() throws Exception {
        mockMvc.perform(delete(CropController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("cropId", activeCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(IllegalArgumentException.class, () -> cropManager.getCropById(activeCrop.getId()));
    }

    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }
}

