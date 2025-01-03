package org.zeros.farm_manager_server.Controller.Data;

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
import org.zeros.farm_manager_server.Controllers.Data.FertilizerController;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.FertilizerDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Services.Interface.Data.FertilizerManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
public class FertilizerControllerTest {
    @Autowired
    FertilizerController fertilizerController;
    @Autowired
    FertilizerManager fertilizerManager;
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
        Fertilizer fertilizer = fertilizerManager.getAllFertilizers(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(FertilizerController.BASE_PATH)
                                .queryParam("id", fertilizer.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(fertilizer.getId().toString())))
                .andExpect(jsonPath("$.name", is(fertilizer.getName())))
                .andReturn();

        displayResponse(result);
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(
                        get(FertilizerController.BASE_PATH)
                                .queryParam("id", UUID.randomUUID().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAll() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FertilizerController.LIST_ALL_PATH)
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
                        get(FertilizerController.LIST_DEFAULT_PATH)
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
                        get(FertilizerController.LIST_USER_PATH)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThanOrEqualTo(0))))
                .andReturn();
        displayResponse(result);

    }

    @Test
    void getByNameAs() throws Exception {
        Fertilizer fertilizer = fertilizerManager.getAllFertilizers(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(FertilizerController.LIST_PARAM_PATH)
                                .param("name", fertilizer.getName().substring(0, 3))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }


    @Test
    void getNaturalFertilizer() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FertilizerController.LIST_PARAM_PATH)
                                .param("isNatural", String.valueOf(true))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);

    }


    @Test
    @Transactional
    void addNew() throws Exception {
        FertilizerDTO fertilizerDTO = FertilizerDTO.builder()
                .producer("TEST32")
                .name("TEST32")
                .isNaturalFertilizer(true)
                .totalCaPercent(30)
                .totalMgPercent(10)
                .build();

        MvcResult result = mockMvc.perform(post(FertilizerController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .get(0).split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        assertThat(fertilizerManager.getFertilizerById(savedUUID).equals(Fertilizer.NONE)).isFalse();
        displayResponse(result);
    }

    @Test
    @Transactional
    void addNewErrorAlreadyExists() throws Exception {


        FertilizerDTO FertilizerDTO = DefaultMappers.fertilizerMapper.entityToDto(
                fertilizerManager.getAllFertilizers(0).getContent().get(2));
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addNewMissingName() throws Exception {
        FertilizerDTO fertilizerDTO = FertilizerDTO.builder()
                .producer("TEST")
                .name("TEST")
                .totalNPercent(10)
                .totalPPercent(30)
                .build();

        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    void update() throws Exception {
        Fertilizer fertilizer = addNewFertilizer();

        FertilizerDTO fertilizerDTO = DefaultMappers.fertilizerMapper.entityToDto(fertilizer);
        fertilizerDTO.setName("TEST_UPDATED");
        fertilizerDTO.setTotalCaPercent(10);
        mockMvc.perform(patch(FertilizerController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(fertilizerManager.getFertilizerById(fertilizer.getId())).isNotNull();
        assertThat(fertilizerManager.getFertilizerById(fertilizer.getId()).getName())
                .isEqualTo("TEST_UPDATED");
        assertThat(fertilizerManager.getFertilizerById(fertilizer.getId()).getTotalCaPercent()).isEqualTo(BigDecimal.valueOf(10));
    }

    private Fertilizer addNewFertilizer() {
        return fertilizerManager.addFertilizer(FertilizerDTO.builder()
                .producer("TEST")
                .name("Test")
                .isNaturalFertilizer(false)
                .totalNPercent(10)
                .totalKPercent(30)
                .build());
    }

    @Test
    @Transactional
    void updateAccessDenied() throws Exception {
        FertilizerDTO fertilizerDTO = DefaultMappers.fertilizerMapper.entityToDto(
                fertilizerManager.getDefaultFertilizers(0).getContent().get(2));
        fertilizerDTO.setName("TEST_UPDATED");
        mockMvc.perform(patch(FertilizerController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void updateModelBlank() throws Exception {
        Fertilizer fertilizer = addNewFertilizer();
        FertilizerDTO FertilizerDTO = DefaultMappers.fertilizerMapper.entityToDto(fertilizer);
        FertilizerDTO.setName("");
        mockMvc.perform(patch(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteFertilizer() throws Exception {
        Fertilizer fertilizer = addNewFertilizer();

        mockMvc.perform(delete(FertilizerController.BASE_PATH)
                        .param("id", fertilizer.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(fertilizerManager.getFertilizerById(fertilizer.getId())).isEqualTo(Fertilizer.NONE);
    }

    @Test
    void deleteFailed() throws Exception {
        Fertilizer fertilizer = fertilizerManager
                .getAllFertilizers(0).getContent().get(0);

        mockMvc.perform(delete(FertilizerController.BASE_PATH)
                        .param("id", fertilizer.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
        assertThat(fertilizerManager.getFertilizerById(fertilizer.getId()).equals(Fertilizer.NONE)).isFalse();

    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

