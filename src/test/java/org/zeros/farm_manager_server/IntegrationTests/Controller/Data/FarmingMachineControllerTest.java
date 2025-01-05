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
import org.zeros.farm_manager_server.Domain.DTO.Data.FarmingMachineDTO;
import org.zeros.farm_manager_server.Domain.Entities.Data.FarmingMachine;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
    }

    @Test
    void getMachineById() throws Exception {
        FarmingMachine testMachine = farmingMachineManager.getAllFarmingMachines(0).getContent().get(3);
        MvcResult result = mockMvc.perform(
                        get(FarmingMachineController.BASE_PATH)
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
                        get(FarmingMachineController.BASE_PATH)
                                .queryParam("id", UUID.randomUUID().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAllFarmingMachines() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FarmingMachineController.LIST_ALL_PATH)
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
        System.out.println(objectMapper.writeValueAsString(farmingMachineDTO));
        MvcResult result = mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmingMachineDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .get(0).split("/");
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmingMachineDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    void updateFarmingMachine() throws Exception {
        FarmingMachine farmingMachineUser = farmingMachineManager.addFarmingMachine(FarmingMachineDTO.builder()
                .supportedOperationTypes(Set.of(OperationType.SEEDING))
                .producer("TEST_TO_UPDATE")
                .model("TEST")
                .build());

        FarmingMachineDTO farmingMachineDTO = DefaultMappers.farmingMachineMapper.entityToDto(farmingMachineUser);
        farmingMachineDTO.setModel("TEST_UPDATED");
        farmingMachineDTO.setDescription(null);
        mockMvc.perform(patch(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmingMachineDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachineDTO.getId())).isNotNull();
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachineDTO.getId()).getModel())
                .isEqualTo("TEST_UPDATED");
    }

    @Test
    @Transactional
    void updateFarmingMachineAccessDenied() throws Exception {
        FarmingMachineDTO farmingMachineDTO = DefaultMappers.farmingMachineMapper.entityToDto(
                farmingMachineManager.getDefaultFarmingMachines(0).getContent().get(2));
        farmingMachineDTO.setModel("TEST_UPDATED");
        mockMvc.perform(patch(FarmingMachineController.BASE_PATH)
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmingMachineDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void deleteFarmingMachine() throws Exception {
        FarmingMachine farmingMachineUser = farmingMachineManager.addFarmingMachine(
                FarmingMachineDTO.builder()
                        .supportedOperationTypes(Set.of(OperationType.SEEDING))
                        .producer("TEST_TO_DELETE")
                        .model("TEST")
                        .build());

        mockMvc.perform(delete(FarmingMachineController.BASE_PATH)
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
        FarmingMachine farmingMachine = farmingMachineManager
                .getAllFarmingMachines(0).getContent().get(0);

        mockMvc.perform(delete(FarmingMachineController.BASE_PATH)
                        .param("id", farmingMachine.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
        assertThat(farmingMachineManager.getFarmingMachineById(farmingMachine.getId()).equals(FarmingMachine.NONE)).isFalse();

    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

