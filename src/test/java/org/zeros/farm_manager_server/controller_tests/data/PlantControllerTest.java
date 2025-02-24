package org.zeros.farm_manager_server.controller_tests.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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
import org.zeros.farm_manager_server.controllers.data.FarmingMachineController;
import org.zeros.farm_manager_server.controllers.data.PlantController;
import org.zeros.farm_manager_server.domain.dto.data.PlantDTO;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.services.interfaces.data.PlantManager;

import java.io.UnsupportedEncodingException;
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
public class PlantControllerTest {
    @Autowired
    PlantManager plantManager;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    void getById() throws Exception {
        PlantDTO plant = findDefaultPlant();
        MvcResult result = mockMvc.perform(
                        get(PlantController.ID_PATH, plant.getId())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(plant.getId().toString())))
                .andExpect(jsonPath("$.variety", is(plant.getVariety())))
                .andExpect(jsonPath("$.species", is(plant.getSpecies().toString())))
                .andReturn();

        displayResponse(result);
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(
                        get(PlantController.ID_PATH, UUID.randomUUID())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAll() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(PlantController.LIST_ALL_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getDefault() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(PlantController.LIST_DEFAULT_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getUserCreated() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(PlantController.LIST_USER_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThanOrEqualTo(0))))
                .andReturn();
        displayResponse(result);

    }

    @Test
    void getByVariety() throws Exception {
        PlantDTO plant = findDefaultPlant();
        MvcResult result = mockMvc.perform(
                        get(PlantController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("variety", plant.getVariety().substring(0, 3))
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }


    @Test
    void getBySpecies() throws Exception {
        PlantDTO plant = findDefaultPlant();

        MvcResult result = mockMvc.perform(
                        get(PlantController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("speciesId", plant.getSpecies().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getByVarietyAndSpecies() throws Exception {
        PlantDTO plant = findDefaultPlant();
        MvcResult result = mockMvc.perform(
                        get(PlantController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("speciesId", plant.getSpecies().toString())
                                .param("variety", plant.getVariety().substring(1, 4))
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void addNew() throws Exception {
        PlantDTO plantWithSameSpecies = findDefaultPlant();
        PlantDTO plantDTO = PlantDTO.builder()
                .variety("Test")
                .species(plantWithSameSpecies.getSpecies())
                .countryOfOrigin("Poland")
                .build();

        MvcResult result = mockMvc.perform(post(PlantController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        assertThat(plantManager.getPlantById(savedUUID).getVariety()).isEqualTo(plantDTO.getVariety());
        displayResponse(result);
    }

    @Test
    void addNewErrorAlreadyExists() throws Exception {

        PlantDTO plantDTO = findDefaultPlant();
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewMissingVariety() throws Exception {
        PlantDTO plantWithSameSpecies = findDefaultPlant();
        PlantDTO plantDTO = PlantDTO.builder()
                .species(plantWithSameSpecies.getSpecies())
                .countryOfOrigin("Poland")
                .build();

        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void update() throws Exception {
        PlantDTO plantDTO = createNewPlant();
        plantDTO.setDescription("TEST_UPDATED");
        plantDTO.setCountryOfOrigin("TEST_UPDATED");
        mockMvc.perform(patch(PlantController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(plantManager.getPlantById(plantDTO.getId())).isNotNull();
        assertThat(plantManager.getPlantById(plantDTO.getId()).getDescription())
                .isEqualTo("TEST_UPDATED");
        assertThat(plantManager.getPlantById(plantDTO.getId()).getCountryOfOrigin())
                .isEqualTo("TEST_UPDATED");
    }

    private PlantDTO createNewPlant() throws Exception {
        PlantDTO plantWithSameSpecies = findDefaultPlant();
        PlantDTO plantDTO = PlantDTO.builder()
                .variety("Test")
                .species(plantWithSameSpecies.getSpecies())
                .countryOfOrigin("Poland")
                .build();

        MvcResult result = mockMvc.perform(post(PlantController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);
        return plantManager.getPlantById(savedUUID);
    }

    @Test
    void updateAccessDenied() throws Exception {
        PlantDTO plantDTO = findDefaultPlant();
        plantDTO.setProductionCompany("TEST_UPDATED");
        mockMvc.perform(patch(PlantController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void updateVarietyBlank() throws Exception {
        PlantDTO plantDTO = createNewPlant();
        plantDTO.setVariety("");
        mockMvc.perform(patch(PlantController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletePlant() throws Exception {
        PlantDTO plant = createNewPlant();
        mockMvc.perform(delete(PlantController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", plant.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(IllegalArgumentException.class, () -> plantManager.getPlantById(plant.getId()));
    }

    @Test
    void deleteFailed() throws Exception {
        PlantDTO plant = findDefaultPlant();

        mockMvc.perform(delete(PlantController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", plant.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    private PlantDTO findDefaultPlant() {
        return plantManager.getDefaultPlants(0).getContent().getFirst();
    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

