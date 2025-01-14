package org.zeros.farm_manager_server.IntegrationTests.Controller.Data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zeros.farm_manager_server.Controllers.Data.FarmingMachineController;
import org.zeros.farm_manager_server.Controllers.Data.FertilizerController;
import org.zeros.farm_manager_server.Domain.DTO.Data.FertilizerDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Fertilizer;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.IntegrationTests.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.Data.FertilizerRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.FertilizerManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Set;
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
    @Autowired
    private FertilizerRepository fertilizerRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    void getById() throws Exception {
        Fertilizer fertilizer = findDefaultFertilizer();
        MvcResult result = mockMvc.perform(
                        get(FertilizerController.ID_PATH,fertilizer.getId())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
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
                        get(FertilizerController.ID_PATH,UUID.randomUUID())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAll() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FertilizerController.LIST_ALL_PATH)
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
                        get(FertilizerController.LIST_DEFAULT_PATH)
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
                        get(FertilizerController.LIST_USER_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThanOrEqualTo(0))))
                .andReturn();
        displayResponse(result);

    }

    @Test
    void getByNameAs() throws Exception {
        Fertilizer fertilizer = findDefaultFertilizer();
        MvcResult result = mockMvc.perform(
                        get(FertilizerController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
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
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("isNatural", String.valueOf(true))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);

    }


    @Test

    void addNew() throws Exception {
        FertilizerDTO fertilizerDTO = FertilizerDTO.builder()
                .producer("TEST32")
                .name("TEST32")
                .isNaturalFertilizer(true)
                .totalCaPercent(BigDecimal.valueOf(30))
                .totalMgPercent(BigDecimal.valueOf(10))
                .build();

        MvcResult result = mockMvc.perform(post(FertilizerController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        assertThat(fertilizerManager.getFertilizerById(savedUUID).equals(Fertilizer.NONE)).isFalse();
        displayResponse(result);
    }

    @Test

    void addNewErrorAlreadyExists() throws Exception {

        FertilizerDTO FertilizerDTO = DefaultMappers.fertilizerMapper.entityToDto(
               findDefaultFertilizer());
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test

    void addNewMissingName() throws Exception {
        FertilizerDTO fertilizerDTO = FertilizerDTO.builder()
                .producer("TEST")
                .name("TEST")
                .totalNPercent(BigDecimal.valueOf(10))
                .totalPPercent(BigDecimal.valueOf(30))
                .build();

        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test

    void update() throws Exception {
        Fertilizer fertilizer = addNewFertilizer();
        FertilizerDTO fertilizerDTO = DefaultMappers.fertilizerMapper.entityToDto(fertilizer);
        fertilizerDTO.setName("TEST_UPDATED");
        fertilizerDTO.setTotalCaPercent(BigDecimal.valueOf(10));
        mockMvc.perform(patch(FertilizerController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(fertilizerManager.getFertilizerById(fertilizer.getId())).isNotNull();
        assertThat(fertilizerManager.getFertilizerById(fertilizer.getId()).getName())
                .isEqualTo("TEST_UPDATED");
        assertThat(fertilizerManager.getFertilizerById(fertilizer.getId()).getTotalCaPercent().floatValue()).isEqualTo(10);
    }

    private Fertilizer addNewFertilizer() throws Exception {
        FertilizerDTO fertilizerDTO = FertilizerDTO.builder()
                .producer("TEST")
                .name("TEST")
                .isNaturalFertilizer(true)
                .totalCaPercent(BigDecimal.valueOf(30))
                .totalMgPercent(BigDecimal.valueOf(10))
                .build();

        MvcResult result = mockMvc.perform(post(FertilizerController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);
        return fertilizerManager.getFertilizerById(savedUUID);
    }

    @Test

    void updateAccessDenied() throws Exception {
        FertilizerDTO fertilizerDTO = DefaultMappers.fertilizerMapper.entityToDto(
                findDefaultFertilizer());
        fertilizerDTO.setName("TEST_UPDATED");
        mockMvc.perform(patch(FertilizerController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test

    void updateModelBlank() throws Exception {
        Fertilizer fertilizer = addNewFertilizer();
        FertilizerDTO FertilizerDTO = DefaultMappers.fertilizerMapper.entityToDto(fertilizer);
        FertilizerDTO.setName("");
        mockMvc.perform(patch(FertilizerController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FertilizerDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test

    void deleteFertilizer() throws Exception {
        Fertilizer fertilizer = addNewFertilizer();

        mockMvc.perform(delete(FertilizerController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", fertilizer.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(fertilizerManager.getFertilizerById(fertilizer.getId())).isEqualTo(Fertilizer.NONE);
    }

    @Test
    void deleteFailed() throws Exception {
        Fertilizer fertilizer = findDefaultFertilizer();

        mockMvc.perform(delete(FertilizerController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", fertilizer.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
        assertThat(fertilizerManager.getFertilizerById(fertilizer.getId()).equals(Fertilizer.NONE)).isFalse();

    }

    private Fertilizer findDefaultFertilizer() {
        return fertilizerRepository.findAllByCreatedByIn(Set.of("ADMIN"), PageRequest.of(0,2)).getContent().getFirst();
    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

