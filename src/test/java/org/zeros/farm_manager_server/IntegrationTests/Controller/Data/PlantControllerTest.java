package org.zeros.farm_manager_server.IntegrationTests.Controller.Data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zeros.farm_manager_server.Controllers.Data.FarmingMachineController;
import org.zeros.farm_manager_server.Controllers.Data.PlantController;
import org.zeros.farm_manager_server.Domain.DTO.Data.PlantDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Plant;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Services.Interface.Data.PlantManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
public class PlantControllerTest {
    @Autowired
    PlantController plantController;
    @Autowired
    PlantManager plantManager;
    @Autowired
    UserManager userManager;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
    }

    @Test
    void getById() throws Exception {
        Plant plant = plantManager.getAllPlants(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(PlantController.BASE_PATH)
                                .queryParam("id", plant.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(plant.getId().toString())))
                .andExpect(jsonPath("$.variety", is(plant.getVariety())))
                .andExpect(jsonPath("$.species",is(plant.getSpecies().getId().toString())))
                .andReturn();

        displayResponse(result);
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(
                        get(PlantController.BASE_PATH)
                                .queryParam("id", UUID.randomUUID().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAll() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(PlantController.LIST_ALL_PATH)
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
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThanOrEqualTo(0))))
                .andReturn();
        displayResponse(result);

    }

    @Test
    void getByVariety() throws Exception {
        Plant plant = plantManager.getAllPlants(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(PlantController.LIST_PARAM_PATH)
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
        Plant plant = plantManager.getAllPlants(0).getContent().get(1);

        MvcResult result = mockMvc.perform(
                        get(PlantController.LIST_PARAM_PATH)
                                .param("speciesId", plant.getSpecies().getId().toString())
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
        Plant plant = plantManager.getAllPlants(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(PlantController.LIST_PARAM_PATH)
                                .param("speciesId", plant.getSpecies().getId().toString())
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
    @Transactional
    void addNew() throws Exception {
        Plant plantWithSameSpecies = plantManager.getAllPlants(0).getContent().get(1);

        PlantDTO plantDTO = PlantDTO.builder()
                .variety("Test")
                .species(plantWithSameSpecies.getSpecies().getId())
                .countryOfOrigin("Poland")
                .build();

        MvcResult result = mockMvc.perform(post(PlantController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .get(0).split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        assertThat(plantManager.getPlantById(savedUUID).equals(Plant.NONE)).isFalse();
        displayResponse(result);
    }

    @Test
    @Transactional
    void addNewErrorAlreadyExists() throws Exception {

        PlantDTO plantDTO = DefaultMappers.plantMapper.entityToDto(
                plantManager.getAllPlants(0).getContent().get(2));
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addNewMissingVariety() throws Exception {
        Plant plantWithSameSpecies = plantManager.getAllPlants(0).getContent().get(1);
        PlantDTO plantDTO = PlantDTO.builder()
                .species(plantWithSameSpecies.getSpecies().getId())
                .countryOfOrigin("Poland")
                .build();

        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    void update() throws Exception {
        Plant plant = createNewPlant();

        PlantDTO plantDTO = DefaultMappers.plantMapper.entityToDto(plant);
        plantDTO.setDescription("TEST_UPDATED");
        plantDTO.setCountryOfOrigin("TEST_UPDATED");
        mockMvc.perform(patch(PlantController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(plantManager.getPlantById(plant.getId())).isNotNull();
        assertThat(plantManager.getPlantById(plant.getId()).getDescription())
                .isEqualTo("TEST_UPDATED");
        assertThat(plantManager.getPlantById(plant.getId()).getCountryOfOrigin())
                .isEqualTo("TEST_UPDATED");
    }

    private Plant createNewPlant() {
        Plant plantWithSameSpecies = plantManager.getAllPlants(0).getContent().get(1);

        return  plantManager.addPlant(PlantDTO.builder()
                .variety("Test")
                .species(plantWithSameSpecies.getSpecies().getId())
                .productionCompany("RAGT")
                .build());
    }

    @Test
    @Transactional
    void updateAccessDenied() throws Exception {
        PlantDTO plantDTO = DefaultMappers.plantMapper.entityToDto(
                plantManager.getDefaultPlants(0).getContent().get(2));
        plantDTO.setProductionCompany("TEST_UPDATED");
        mockMvc.perform(patch(PlantController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void updateVarietyBlank() throws Exception {
        Plant plant = createNewPlant();

        PlantDTO plantDTO = DefaultMappers.plantMapper.entityToDto(plant);
        plantDTO.setVariety("");
        mockMvc.perform(patch(PlantController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deletePlant() throws Exception {
        Plant plant = createNewPlant();

        mockMvc.perform(delete(PlantController.BASE_PATH)
                        .param("id", plant.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(plantManager.getPlantById(plant.getId())).isEqualTo(Plant.NONE);
    }

    @Test
    void deleteFailed() throws Exception {
        Plant plant = plantManager
                .getAllPlants(0).getContent().get(0);

        mockMvc.perform(delete(PlantController.BASE_PATH)
                        .param("id", plant.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
        assertThat(plantManager.getPlantById(plant.getId()).equals(Plant.NONE)).isFalse();

    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

