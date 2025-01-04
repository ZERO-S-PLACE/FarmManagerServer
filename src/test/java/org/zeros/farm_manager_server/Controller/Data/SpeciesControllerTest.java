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
import org.zeros.farm_manager_server.Controllers.Data.SpeciesController;
import org.zeros.farm_manager_server.Domain.DTO.Data.SpeciesDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
public class SpeciesControllerTest {
    @Autowired
    SpeciesController speciesController;
    @Autowired
    SpeciesManager speciesManager;
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
        Species species = speciesManager.getAllSpecies(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(SpeciesController.BASE_PATH)
                                .queryParam("id", species.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(species.getId().toString())))
                .andExpect(jsonPath("$.name", is(species.getName())))
                .andReturn();

        displayResponse(result);
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(
                        get(SpeciesController.BASE_PATH)
                                .queryParam("id", UUID.randomUUID().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAll() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(SpeciesController.LIST_ALL_PATH)
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
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThanOrEqualTo(0))))
                .andReturn();
        displayResponse(result);

    }

    @Test
    void getByNameAs() throws Exception {
        Species species = speciesManager.getAllSpecies(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(SpeciesController.LIST_PARAM_PATH)
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
        Species species = speciesManager.getAllSpecies(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(SpeciesController.LIST_PARAM_PATH)
                                .param("family", species.getFamily())
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
        SpeciesDTO speciesDTO = SpeciesDTO.builder()
                .family("TEST")
                .name("TEST")
                .build();

        MvcResult result = mockMvc.perform(post(SpeciesController.BASE_PATH)
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

        assertThat(speciesManager.getSpeciesById(savedUUID).equals(Species.NONE)).isFalse();
        displayResponse(result);
    }

    @Test
    @Transactional
    void addNewErrorAlreadyExists() throws Exception {

        SpeciesDTO speciesDTO = DefaultMappers.speciesMapper.entityToDto(
                speciesManager.getAllSpecies(0).getContent().get(2));
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speciesDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addNewMissingName() throws Exception {
        SpeciesDTO speciesDTO = SpeciesDTO.builder()
                .family("TEST")
                .name("TEST")
                .build();

        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speciesDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    void update() throws Exception {
        Species species = saveNewSpecies();
        SpeciesDTO speciesDTO = DefaultMappers.speciesMapper.entityToDto(species);
        speciesDTO.setName("TEST_UPDATED");
        speciesDTO.setDescription("DESCRIPTION_UPDATED");
        mockMvc.perform(patch(SpeciesController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speciesDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        Species speciesUpdated = speciesManager.getSpeciesById(species.getId());
        assertThat(speciesUpdated).isNotNull();
        assertThat(speciesUpdated.getName()).isEqualTo("TEST_UPDATED");
        assertThat(speciesUpdated.getDescription()).isEqualTo("DESCRIPTION_UPDATED");
    }

    private Species saveNewSpecies() {
        return speciesManager.addSpecies(SpeciesDTO.builder()
                .family("TEST")
                .name("TEST")
                .build());
    }

    @Test
    @Transactional
    void updateAccessDenied() throws Exception {
        SpeciesDTO speciesDTO = DefaultMappers.speciesMapper.entityToDto(
                speciesManager.getDefaultSpecies(0).getContent().get(2));
        speciesDTO.setName("TEST_UPDATED");
        mockMvc.perform(patch(SpeciesController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speciesDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void updateModelBlank() throws Exception {
        Species species = saveNewSpecies();
        SpeciesDTO speciesDTO = DefaultMappers.speciesMapper.entityToDto(species);
        speciesDTO.setName("");
        mockMvc.perform(patch(SpeciesController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speciesDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteSpecies() throws Exception {
        Species species = saveNewSpecies();

        mockMvc.perform(delete(SpeciesController.BASE_PATH)
                        .param("id", species.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(speciesManager.getSpeciesById(species.getId())).isEqualTo(Species.NONE);
    }

    @Test
    void deleteFailed() throws Exception {
        Species species = speciesManager.getAllSpecies(0).getContent().getFirst();

        mockMvc.perform(delete(SpeciesController.BASE_PATH)
                        .param("id", species.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
        assertThat(speciesManager.getSpeciesById(species.getId()).equals(Species.NONE)).isFalse();

    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

