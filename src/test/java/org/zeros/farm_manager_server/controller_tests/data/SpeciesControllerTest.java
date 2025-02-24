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
import org.zeros.farm_manager_server.controllers.data.SpeciesController;
import org.zeros.farm_manager_server.domain.dto.data.SpeciesDTO;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.services.interfaces.data.SpeciesManager;

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
public class SpeciesControllerTest {
    @Autowired
    SpeciesManager speciesManager;
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
        SpeciesDTO species = findDefaultSpecies();
        MvcResult result = mockMvc.perform(
                        get(SpeciesController.ID_PATH, species.getId())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(species.getId().toString())))
                .andExpect(jsonPath("$.name", is(species.getName())))
                .andReturn();

        displayResponse(result);
    }

    private SpeciesDTO findDefaultSpecies() {
        return speciesManager.getDefaultSpecies(0).getContent().getFirst();
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(
                        get(SpeciesController.ID_PATH, UUID.randomUUID())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAll() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(SpeciesController.LIST_ALL_PATH)
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
                        get(SpeciesController.LIST_DEFAULT_PATH)
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
                        get(SpeciesController.LIST_USER_PATH)
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
        SpeciesDTO species = findDefaultSpecies();
        MvcResult result = mockMvc.perform(
                        get(SpeciesController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("name", species.getName().substring(0, 3))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }


    @Test
    void getByFamily() throws Exception {
        SpeciesDTO species = findDefaultSpecies();
        MvcResult result = mockMvc.perform(
                        get(SpeciesController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("family", species.getFamily())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);

    }


    @Test
    void addNew() throws Exception {
        SpeciesDTO speciesDTO = SpeciesDTO.builder()
                .family("TEST")
                .name("TEST")
                .build();

        MvcResult result = mockMvc.perform(post(SpeciesController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speciesDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        assertThat(speciesManager.getSpeciesById(savedUUID).getFamily()).isEqualTo(speciesDTO.getFamily());
        displayResponse(result);
    }

    @Test
    void addNewErrorAlreadyExists() throws Exception {

        SpeciesDTO speciesDTO = findDefaultSpecies();
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speciesDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewMissingName() throws Exception {
        SpeciesDTO speciesDTO = SpeciesDTO.builder()
                .family("TEST")
                .name("TEST")
                .build();

        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speciesDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void update() throws Exception {
        SpeciesDTO speciesDTO = saveNewSpecies();
        speciesDTO.setName("TEST_UPDATED");
        speciesDTO.setDescription("DESCRIPTION_UPDATED");
        mockMvc.perform(patch(SpeciesController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speciesDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        SpeciesDTO speciesUpdated = speciesManager.getSpeciesById(speciesDTO.getId());
        assertThat(speciesUpdated).isNotNull();
        assertThat(speciesUpdated.getName()).isEqualTo("TEST_UPDATED");
        assertThat(speciesUpdated.getDescription()).isEqualTo("DESCRIPTION_UPDATED");
    }

    private SpeciesDTO saveNewSpecies() throws Exception {
        SpeciesDTO speciesDTO = SpeciesDTO.builder()
                .family("TEST")
                .name("TEST")
                .build();

        MvcResult result = mockMvc.perform(post(SpeciesController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speciesDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);
        return speciesManager.getSpeciesById(savedUUID);
    }

    @Test
    void updateAccessDenied() throws Exception {
        SpeciesDTO speciesDTO = findDefaultSpecies();
        speciesDTO.setName("TEST_UPDATED");
        mockMvc.perform(patch(SpeciesController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speciesDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void updateModelBlank() throws Exception {
        SpeciesDTO speciesDTO = saveNewSpecies();
        speciesDTO.setName("");
        mockMvc.perform(patch(SpeciesController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speciesDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSpecies() throws Exception {
        SpeciesDTO species = saveNewSpecies();

        mockMvc.perform(delete(SpeciesController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", species.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(IllegalArgumentException.class, () -> speciesManager.getSpeciesById(species.getId()));
    }

    @Test
    void deleteFailed() throws Exception {
        SpeciesDTO species = findDefaultSpecies();

        mockMvc.perform(delete(SpeciesController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", species.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

