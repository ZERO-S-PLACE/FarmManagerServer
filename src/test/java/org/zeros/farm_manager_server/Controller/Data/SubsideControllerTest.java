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
import org.zeros.farm_manager_server.Controllers.Data.SprayController;
import org.zeros.farm_manager_server.Domain.DTO.AgriculturalOperations.Data.SprayDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.SprayType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Services.Interface.Data.SprayManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
public class SubsideControllerTest {
    @Autowired
    SprayController sprayController;
    @Autowired
    SprayManager sprayManager;
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
        Spray spray = sprayManager.getAllSprays(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(SprayController.BASE_PATH)
                                .queryParam("id", spray.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(spray.getId().toString())))
                .andExpect(jsonPath("$.name", is(spray.getName())))
                .andReturn();

        displayResponse(result);
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(
                        get(SprayController.BASE_PATH)
                                .queryParam("id", UUID.randomUUID().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAll() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(SprayController.LIST_ALL_PATH)
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
                        get(SprayController.LIST_DEFAULT_PATH)
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
                        get(SprayController.LIST_USER_PATH)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThanOrEqualTo(0))))
                .andReturn();
        displayResponse(result);

    }

    @Test
    void getByNameAs() throws Exception {
        Spray spray = sprayManager.getAllSprays(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(SprayController.LIST_PARAM_PATH)
                                .param("name", spray.getName().substring(0,3))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }


    @Test
    void getBySprayType() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(SprayController.LIST_PARAM_PATH)
                                .param("sprayType", SprayType.INSECTICIDE.toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);

    }

    @Test
    void getByActiveSubstance() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(SprayController.LIST_PARAM_PATH)
                                .param("activeSubstance", "X1")
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
        SprayDTO sprayDTO = SprayDTO.builder()
                .activeSubstances(Set.of("X1"))
                .producer("TEST32")
                .name("TEST32")
                .build();

        MvcResult result = mockMvc.perform(post(SprayController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .get(0).split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        assertThat(sprayManager.getSprayById(savedUUID).equals(Spray.NONE)).isFalse();
        displayResponse(result);
    }

    @Test
    @Transactional
    void addNewErrorAlreadyExists() throws Exception {


        SprayDTO sprayDTO = DefaultMappers.sprayMapper.entityToDto(
                sprayManager.getAllSprays(0).getContent().get(2));
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addNewMissingName() throws Exception {
        SprayDTO sprayDTO = SprayDTO.builder()
                .activeSubstances(Set.of("X1"))
                .producer("TEST")
                .name("TEST")
                .build();

        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    void update() throws Exception {
        Spray spray = Spray.builder()
                .activeSubstances(Set.of("X1"))
                .producer("TEST33")
                .name("TEST33")
                .build();
        spray = sprayManager.addSpray(spray);

        SprayDTO sprayDTO = DefaultMappers.sprayMapper.entityToDto(spray);
        sprayDTO.setName("TEST_UPDATED");
        sprayDTO.setDescription(null);
        sprayDTO.getActiveSubstances().add("X2");
        mockMvc.perform(patch(SprayController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(sprayManager.getSprayById(spray.getId())).isNotNull();
        assertThat(sprayManager.getSprayById(spray.getId()).getName())
                .isEqualTo("TEST_UPDATED");
        assertThat(sprayManager.getSprayById(spray.getId()).getActiveSubstances())
                .contains("X2");
    }

    @Test
    @Transactional
    void updateAccessDenied() throws Exception {
        SprayDTO sprayDTO = DefaultMappers.sprayMapper.entityToDto(
                sprayManager.getDefaultSprays(0).getContent().get(2));
        sprayDTO.setName("TEST_UPDATED");
        mockMvc.perform(patch(SprayController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void updateModelBlank() throws Exception {
        Spray spray = Spray.builder()
                .activeSubstances(Set.of("X1"))
                .producer("TEST33")
                .name("TEST33")
                .build();
        spray = sprayManager.addSpray(spray);

        SprayDTO sprayDTO = DefaultMappers.sprayMapper.entityToDto(spray);
        sprayDTO.setName("");
        mockMvc.perform(patch(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteSpray() throws Exception {
        Spray spray = Spray.builder()
                .activeSubstances(Set.of("X1"))
                .producer("TEST32")
                .name("TEST32")
                .build();
        spray = sprayManager.addSpray(spray);

        mockMvc.perform(delete(SprayController.BASE_PATH)
                        .param("id", spray.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(sprayManager.getSprayById(spray.getId())).isEqualTo(Spray.NONE);
    }

    @Test
    void deleteFailed() throws Exception {
        Spray spray = sprayManager
                .getAllSprays(0).getContent().get(0);

        mockMvc.perform(delete(SprayController.BASE_PATH)
                        .param("id", spray.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
        assertThat(sprayManager.getSprayById(spray.getId()).equals(Spray.NONE)).isFalse();

    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

