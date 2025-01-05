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
import org.zeros.farm_manager_server.Controllers.Data.SubsideController;
import org.zeros.farm_manager_server.Domain.DTO.Data.SubsideDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.Subside;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;
import org.zeros.farm_manager_server.Services.Interface.Data.SubsideManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
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
    SubsideController subsideController;
    @Autowired
    SubsideManager subsideManager;
    @Autowired
    UserManager userManager;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private SpeciesManager speciesManager;
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
        Subside subside = subsideManager.getAllSubsides(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(SubsideController.BASE_PATH)
                                .queryParam("id", subside.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(subside.getId().toString())))
                .andExpect(jsonPath("$.name", is(subside.getName())))
                .andReturn();

        displayResponse(result);
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(
                        get(SubsideController.BASE_PATH)
                                .queryParam("id", UUID.randomUUID().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAll() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(SubsideController.LIST_ALL_PATH)
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
                        get(SubsideController.LIST_DEFAULT_PATH)
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
                        get(SubsideController.LIST_USER_PATH)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThanOrEqualTo(0))))
                .andReturn();
        displayResponse(result);

    }

    @Test
    void getByNameAs() throws Exception {
        Subside subside = subsideManager.getAllSubsides(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(SubsideController.LIST_PARAM_PATH)
                                .param("name", subside.getName().substring(0, 3))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }


    @Test
    @Transactional
    void getBySpeciesAllowed() throws Exception {
        Subside subside = subsideManager.getAllSubsides(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(SubsideController.LIST_PARAM_PATH)
                                .param("speciesId", String.valueOf(
                                        subside.getSpeciesAllowed().stream().toList().getFirst().getId()))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }
    @Test
    @Transactional
    void getByNameAndSpeciesAllowed() throws Exception {
        Subside subside = subsideManager.getAllSubsides(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(SubsideController.LIST_PARAM_PATH)
                                .param("name", subside.getName().substring(0, 3))
                                .param("speciesId", String.valueOf(
                                        subside.getSpeciesAllowed().stream().toList().getFirst().getId()))
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
        SubsideDTO subsideDTO = SubsideDTO.builder()
                .name("TEST")
                .yearOfSubside(LocalDate.now())
                .subsideValuePerAreaUnit(321.21f)
                .speciesAllowed(Set.of(speciesManager.getAllSpecies(0).getContent().get(3).getId()))
                .build();

        MvcResult result = mockMvc.perform(post(SubsideController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsideDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        assertThat(subsideManager.getSubsideById(savedUUID).equals(Subside.NONE)).isFalse();
        displayResponse(result);
    }

    @Test
    @Transactional
    void addNewErrorAlreadyExists() throws Exception {

        SubsideDTO subsideDTO = DefaultMappers.subsideMapper.entityToDto(
                subsideManager.getAllSubsides(0).getContent().get(2));
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsideDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addNewMissingName() throws Exception {
        SubsideDTO subsideDTO = SubsideDTO.builder()
                .yearOfSubside(LocalDate.now())
                .subsideValuePerAreaUnit(321.21f)
                .speciesAllowed(Set.of(speciesManager.getAllSpecies(0).getContent().get(3).getId()))
                .build();


        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsideDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    void update() throws Exception {
        Subside subside = saveNewSubside();
        SubsideDTO subsideDTO = DefaultMappers.subsideMapper.entityToDto(subside);
        subsideDTO.setName("TEST_UPDATED");
        subsideDTO.setDescription("DESCRIPTION_UPDATED");
        mockMvc.perform(patch(SubsideController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsideDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        Subside subsideUpdated = subsideManager.getSubsideById(subside.getId());
        assertThat(subsideUpdated).isNotNull();
        assertThat(subsideUpdated.getName()).isEqualTo("TEST_UPDATED");
        assertThat(subsideUpdated.getDescription()).isEqualTo("DESCRIPTION_UPDATED");
    }

    private Subside saveNewSubside() {
        return subsideManager.addSubside(SubsideDTO.builder()
                .name("TEST")
                .yearOfSubside(LocalDate.now())
                .subsideValuePerAreaUnit(321.21f)
                .speciesAllowed(Set.of(speciesManager.getAllSpecies(0).getContent().get(3).getId()))
                .build());

    }

    @Test
    @Transactional
    void updateAccessDenied() throws Exception {
        SubsideDTO subsideDTO = DefaultMappers.subsideMapper.entityToDto(
                subsideManager.getDefaultSubsides(0).getContent().get(2));
        subsideDTO.setName("TEST_UPDATED");
        mockMvc.perform(patch(SubsideController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsideDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void updateModelBlank() throws Exception {
        Subside subside = saveNewSubside();
        SubsideDTO subsideDTO = DefaultMappers.subsideMapper.entityToDto(subside);
        subsideDTO.setName("");
        mockMvc.perform(patch(SubsideController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsideDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteSubside() throws Exception {
        Subside subside = saveNewSubside();

        mockMvc.perform(delete(SubsideController.BASE_PATH)
                        .param("id", subside.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(subsideManager.getSubsideById(subside.getId())).isEqualTo(Subside.NONE);
    }

    @Test
    void deleteFailed() throws Exception {
        Subside subside = subsideManager.getAllSubsides(0).getContent().getFirst();

        mockMvc.perform(delete(SubsideController.BASE_PATH)
                        .param("id", subside.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
        assertThat(subsideManager.getSubsideById(subside.getId()).equals(Subside.NONE)).isFalse();

    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

