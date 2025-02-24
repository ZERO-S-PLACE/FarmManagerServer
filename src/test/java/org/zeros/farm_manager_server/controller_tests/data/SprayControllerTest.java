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
import org.zeros.farm_manager_server.controllers.data.SprayController;
import org.zeros.farm_manager_server.domain.dto.data.SprayDTO;
import org.zeros.farm_manager_server.domain.enums.SprayType;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.services.interfaces.data.SprayManager;

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
public class SprayControllerTest {

    @Autowired
    SprayManager sprayManager;
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
        SprayDTO spray = findDefaultSpray();
        MvcResult result = mockMvc.perform(
                        get(SprayController.ID_PATH, spray.getId())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
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
                        get(SprayController.ID_PATH, UUID.randomUUID())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAll() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(SprayController.LIST_ALL_PATH)
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
                        get(SprayController.LIST_DEFAULT_PATH)
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
                        get(SprayController.LIST_USER_PATH)
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
        SprayDTO spray = findDefaultSpray();
        MvcResult result = mockMvc.perform(
                        get(SprayController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("name", spray.getName().substring(0, 3))
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
                                .with(JWT_Authentication.jwtRequestPostProcessor)
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
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("activeSubstance", "X1")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void addNew() throws Exception {
        SprayDTO sprayDTO = SprayDTO.builder()
                .activeSubstances(Set.of("X1"))
                .producer("TEST")
                .name("TEST")
                .sprayType(SprayType.HERBICIDE)
                .build();

        MvcResult result = mockMvc.perform(post(SprayController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        assertThat(sprayManager.getSprayById(savedUUID).getName()).isEqualTo(sprayDTO.getName());
        displayResponse(result);
    }

    @Test
    void addNewErrorAlreadyExists() throws Exception {


        SprayDTO sprayDTO = findDefaultSpray();
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewMissingName() throws Exception {
        SprayDTO sprayDTO = SprayDTO.builder()
                .activeSubstances(Set.of("X1"))
                .producer("TEST")
                .build();

        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void update() throws Exception {
        SprayDTO sprayDTO = saveNewSpray();
        sprayDTO.setName("TEST_UPDATED");
        sprayDTO.setDescription(null);
        sprayDTO.getActiveSubstances().add("X2");
        mockMvc.perform(patch(SprayController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(sprayManager.getSprayById(sprayDTO.getId())).isNotNull();
        assertThat(sprayManager.getSprayById(sprayDTO.getId()).getName())
                .isEqualTo("TEST_UPDATED");
        assertThat(sprayManager.getSprayById(sprayDTO.getId()).getActiveSubstances())
                .contains("X2");
    }

    private SprayDTO saveNewSpray() throws Exception {
        SprayDTO sprayDTO = SprayDTO.builder()
                .activeSubstances(Set.of("X1"))
                .producer("TEST")
                .name("TEST")
                .sprayType(SprayType.HERBICIDE)
                .build();

        MvcResult result = mockMvc.perform(post(SprayController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);
        return sprayManager.getSprayById(savedUUID);
    }

    @Test
    void updateAccessDenied() throws Exception {
        SprayDTO sprayDTO = findDefaultSpray();
        sprayDTO.setName("TEST_UPDATED");
        mockMvc.perform(patch(SprayController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void updateModelBlank() throws Exception {
        SprayDTO sprayDTO = saveNewSpray();
        sprayDTO.setName("");
        mockMvc.perform(patch(SprayController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sprayDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSpray() throws Exception {
        SprayDTO spray = saveNewSpray();
        mockMvc.perform(delete(SprayController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", spray.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(IllegalArgumentException.class, () -> sprayManager.getSprayById(spray.getId()));
    }

    @Test
    void deleteFailed() throws Exception {
        SprayDTO spray = findDefaultSpray();

        mockMvc.perform(delete(SprayController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", spray.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    private SprayDTO findDefaultSpray() {
        return sprayManager.getDefaultSprays(0).getContent().getFirst();
    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

