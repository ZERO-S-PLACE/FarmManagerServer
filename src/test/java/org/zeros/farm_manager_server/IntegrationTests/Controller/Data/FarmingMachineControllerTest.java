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
import org.zeros.farm_manager_server.Domain.DTO.Data.FarmingMachineDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.IntegrationTests.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.Data.FarmingMachineRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.FarmingMachineManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class FarmingMachineControllerTest {
    @Autowired
    FarmingMachineController farmingMachineController;
    @Autowired
    FarmingMachineManager farmingMachineManager;
    @Autowired
    UserManager userManager;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;
    MockMvc mockMvc;
    @Autowired
    private FarmingMachineRepository farmingMachineRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    void getMachineById() throws Exception {
        FarmingMachine testMachine = findAnyFarmingMachine();
        MvcResult result = mockMvc.perform(
                        get(FarmingMachineController.ID_PATH,testMachine.getId())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .queryParam("id", testMachine.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testMachine.getId().toString())))
                .andExpect(jsonPath("$.model", is(testMachine.getModel())))
                .andReturn();

        displayResponse(result);
    }

    @Test
    void getMachineByIdNotFound() throws Exception {
        mockMvc.perform(
                        get(FarmingMachineController.ID_PATH,UUID.randomUUID())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAllFarmingMachines() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FarmingMachineController.LIST_ALL_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getDefaultFarmingMachines() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FarmingMachineController.LIST_DEFAULT_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getUserFarmingMachines() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FarmingMachineController.LIST_USER_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThanOrEqualTo(0))))
                .andReturn();
        displayResponse(result);

    }

    @Test
    void getFarmingMachineByModelAs() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FarmingMachineController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("model", "Trion")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getFarmingMachineByProducerAs() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FarmingMachineController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("producer", "Class")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThanOrEqualTo(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getFarmingMachineByProducerAndNameAs() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FarmingMachineController.LIST_PARAM_PATH)
                                .param("producer", "Class")
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("model", "Trion")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);

    }


    @Test
    void getFarmingMachineBySupportedOperation() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FarmingMachineController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("operationType", "HARVEST")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    @Transactional
    void addFarmingMachine() throws Exception {
        FarmingMachineDTO farmingMachineDTO = FarmingMachineDTO.builder()
                .supportedOperationTypes(Set.of(OperationType.SEEDING))
                .producer("TEST2")
                .model("TEST2")
                .build();

        MvcResult result = mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmingMachineDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        assertThat(farmingMachineManager.getFarmingMachineById(savedUUID).equals(FarmingMachine.NONE)).isFalse();
        displayResponse(result);
    }

    @Test
    @Transactional
    void addFarmingMachineErrorMachineExists() throws Exception {
        FarmingMachineDTO farmingMachineDTO = DefaultMappers.farmingMachineMapper.entityToDto(
                farmingMachineManager.getDefaultFarmingMachines(0).getContent().get(2));
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmingMachineDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addFarmingMachineMissingModel() throws Exception {
        FarmingMachineDTO farmingMachineDTO = DefaultMappers.farmingMachineMapper.entityToDto(
                farmingMachineManager.getDefaultFarmingMachines(0).getContent().get(2));
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmingMachineDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    void updateFarmingMachine() throws Exception {
        FarmingMachine farmingMachineUser = saveNewFarmingMachine();

        FarmingMachineDTO farmingMachineDTO = DefaultMappers.farmingMachineMapper.entityToDto(farmingMachineUser);
        farmingMachineDTO.setModel("TEST_UPDATED");
        farmingMachineDTO.setDescription(null);
        mockMvc.perform(patch(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmingMachineDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachineDTO.getId())).isNotNull();
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachineDTO.getId()).getModel())
                .isEqualTo("TEST_UPDATED");
    }

    private FarmingMachine saveNewFarmingMachine() throws Exception {
        FarmingMachineDTO farmingMachineDTO=FarmingMachineDTO.builder()
                .supportedOperationTypes(Set.of(OperationType.SEEDING))
                .producer("TEST_TO_UPDATE")
                .model("TEST")
                .build();

        MvcResult result = mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmingMachineDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);
        return farmingMachineManager.getFarmingMachineById(savedUUID);
    }

    @Test
    @Transactional
    void updateFarmingMachineAccessDenied() throws Exception {
        FarmingMachineDTO farmingMachineDTO = DefaultMappers.farmingMachineMapper.entityToDto(
                farmingMachineManager.getDefaultFarmingMachines(0).getContent().get(2));
        farmingMachineDTO.setModel("TEST_UPDATED");
        mockMvc.perform(patch(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmingMachineDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void updateFarmingMachineModelBlank() throws Exception {
        FarmingMachineDTO farmingMachineDTO = DefaultMappers.farmingMachineMapper.entityToDto(
                farmingMachineManager.getDefaultFarmingMachines(0).getContent().get(2));
        farmingMachineDTO.setModel("");
        mockMvc.perform(patch(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmingMachineDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void deleteFarmingMachine() throws Exception {
        FarmingMachine farmingMachineUser = saveNewFarmingMachine();

        mockMvc.perform(delete(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", farmingMachineUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachineUser.getId())).isEqualTo(FarmingMachine.NONE);
    }

    @Test
    @Transactional
    void deleteFarmingMachineFailed() throws Exception {
        FarmingMachine farmingMachine = findAnyFarmingMachine();

        mockMvc.perform(delete(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", farmingMachine.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachine.getId()).equals(FarmingMachine.NONE)).isFalse();

    }

    private FarmingMachine findAnyFarmingMachine() {
        return farmingMachineRepository.findAllByCreatedByIn(Set.of("ADMIN"), PageRequest.of(0,2))
                .stream().findFirst().get();
    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

