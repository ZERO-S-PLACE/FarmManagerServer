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
import org.zeros.farm_manager_server.controllers.data.SubsideController;
import org.zeros.farm_manager_server.domain.dto.data.SubsideDTO;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.services.interfaces.data.SpeciesManager;
import org.zeros.farm_manager_server.services.interfaces.data.SubsideManager;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class SubsideControllerTest {

    @Autowired
    SubsideManager subsideManager;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;
    MockMvc mockMvc;
    @Autowired
    private SpeciesManager speciesManager;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    void getById() throws Exception {
        SubsideDTO subside = findDefaultSubside();
        MvcResult result = mockMvc.perform(
                        get(SubsideController.ID_PATH, subside.getId())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
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
                        get(SubsideController.ID_PATH, UUID.randomUUID())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAll() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(SubsideController.LIST_ALL_PATH)
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
                        get(SubsideController.LIST_DEFAULT_PATH)
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
                        get(SubsideController.LIST_USER_PATH)
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
        SubsideDTO subside = findDefaultSubside();
        MvcResult result = mockMvc.perform(
                        get(SubsideController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("name", subside.getName().substring(0, 3))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }


    @Test
    void getBySpeciesAllowed() throws Exception {
        SubsideDTO subside = findDefaultSubside();
        MvcResult result = mockMvc.perform(
                        get(SubsideController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("speciesId", String.valueOf(
                                        subside.getSpeciesAllowed().stream().toList().getFirst()))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getByNameAndSpeciesAllowed() throws Exception {
        SubsideDTO subside = findDefaultSubside();
        MvcResult result = mockMvc.perform(
                        get(SubsideController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("name", subside.getName().substring(0, 3))
                                .param("speciesId", String.valueOf(
                                        subside.getSpeciesAllowed().stream().toList().getFirst()))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }


    @Test
    void addNew() throws Exception {
        SubsideDTO subsideDTO = SubsideDTO.builder()
                .name("TEST")
                .yearOfSubside(LocalDate.now())
                .subsideValuePerAreaUnit(BigDecimal.valueOf(321.21))
                .speciesAllowed(Set.of(speciesManager.getDefaultSpecies(0).getContent().get(3).getId()))
                .build();

        MvcResult result = mockMvc.perform(post(SubsideController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
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

        assertThat(subsideManager.getSubsideById(savedUUID).getName()).isEqualTo(subsideDTO.getName());
        displayResponse(result);
    }

    @Test
    void addNewErrorAlreadyExists() throws Exception {

        SubsideDTO subsideDTO = findDefaultSubside();
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsideDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewMissingName() throws Exception {
        SubsideDTO subsideDTO = SubsideDTO.builder()
                .yearOfSubside(LocalDate.now())
                .subsideValuePerAreaUnit(BigDecimal.valueOf(321.21))
                .speciesAllowed(Set.of(speciesManager.getDefaultSpecies(0).getContent().get(3).getId()))
                .build();


        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsideDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void update() throws Exception {
        SubsideDTO subsideDTO = saveNewSubside();
        subsideDTO.setName("TEST_UPDATED");
        subsideDTO.setDescription("DESCRIPTION_UPDATED");
        mockMvc.perform(patch(SubsideController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsideDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        SubsideDTO subsideUpdated = subsideManager.getSubsideById(subsideDTO.getId());
        assertThat(subsideUpdated).isNotNull();
        assertThat(subsideUpdated.getName()).isEqualTo("TEST_UPDATED");
        assertThat(subsideUpdated.getDescription()).isEqualTo("DESCRIPTION_UPDATED");
    }

    private SubsideDTO saveNewSubside() throws Exception {
        SubsideDTO subsideDTO = SubsideDTO.builder()
                .name("TEST")
                .yearOfSubside(LocalDate.now())
                .subsideValuePerAreaUnit(BigDecimal.valueOf(321.21))
                .speciesAllowed(Set.of(speciesManager.getDefaultSpecies(0).getContent().get(3).getId()))
                .build();

        MvcResult result = mockMvc.perform(post(SubsideController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
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
        return subsideManager.getSubsideById(savedUUID);

    }

    @Test
    void updateAccessDenied() throws Exception {
        SubsideDTO subsideDTO = findDefaultSubside();
        subsideDTO.setName("TEST_UPDATED");
        mockMvc.perform(patch(SubsideController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsideDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void updateModelBlank() throws Exception {
        SubsideDTO subsideDTO = saveNewSubside();
        subsideDTO.setName("");
        mockMvc.perform(patch(SubsideController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsideDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSubside() throws Exception {
        SubsideDTO subside = saveNewSubside();

        mockMvc.perform(delete(SubsideController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", subside.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(IllegalArgumentException.class, () -> subsideManager.getSubsideById(subside.getId()));
    }

    @Test
    void deleteFailed() throws Exception {
        SubsideDTO subside = findDefaultSubside();

        mockMvc.perform(delete(SubsideController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", subside.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    private SubsideDTO findDefaultSubside() {
        return subsideManager.getDefaultSubsides(0).getContent().getFirst();
    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

