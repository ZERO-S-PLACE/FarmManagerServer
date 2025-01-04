package org.zeros.farm_manager_server.Controller;

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
import org.zeros.farm_manager_server.Controllers.CropParametersController;
import org.zeros.farm_manager_server.Controllers.Data.FarmingMachineController;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.GrainParametersDTO;
import org.zeros.farm_manager_server.Domain.Entities.AgriculturalOperations.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.GrainParameters;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Services.Interface.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
public class CropParametersControllerTest {
    @Autowired
    CropParametersController cropParametersController;
    @Autowired
    CropParametersManager cropParametersManager;
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
        CropParameters cropParameters = cropParametersManager.getAllCropParameters(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(CropParametersController.BASE_PATH)
                                .queryParam("id", cropParameters.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(cropParameters.getId().toString())))
                .andExpect(jsonPath("$.name", is(cropParameters.getName())))
                .andReturn();

        displayResponse(result);
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get(CropParametersController.BASE_PATH)
                        .queryParam("id", UUID.randomUUID().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getAll() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(CropParametersController.LIST_ALL_PATH)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getByNameAs() throws Exception {
        CropParameters cropParameters = cropParametersManager.getAllCropParameters(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(CropParametersController.LIST_PARAM_PATH)
                                .param("name", cropParameters.getName().substring(0, 3))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }


    @Test
    void getByResourceType() throws Exception {
        CropParameters cropParameters = cropParametersManager.getAllCropParameters(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(CropParametersController.LIST_PARAM_PATH)
                                .param("resourceType", String.valueOf(cropParameters.getResourceType()))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getByNameAndResourceType() throws Exception {
        CropParameters cropParameters = cropParametersManager.getAllCropParameters(0).getContent().get(1);
        MvcResult result = mockMvc.perform(
                        get(CropParametersController.LIST_PARAM_PATH)
                                .param("name", cropParameters.getName().substring(0, 3))
                                .param("resourceType", String.valueOf(cropParameters.getResourceType()))
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
        CropParametersDTO cropParametersDTO = GrainParametersDTO.builder()
                .name("TEST")
                .resourceType(ResourceType.GRAIN)
                .pollution(0.95f)
                .density(800)
                .fallingNumber(333)
                .build();

        MvcResult result = mockMvc.perform(post(CropParametersController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropParametersDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        CropParameters cropParameters=cropParametersManager.getCropParametersById(savedUUID);
        assertThat(cropParameters).isNotEqualTo(CropParameters.NONE);
        assertThat(cropParameters.getName()).isEqualTo(cropParametersDTO.getName());
        assertThat(cropParameters.getResourceType()).isEqualTo(cropParametersDTO.getResourceType());
        assertThat(((GrainParameters)cropParameters).getDensity().floatValue()).isEqualTo(800);
        displayResponse(result);
    }

    @Test
    @Transactional
    void addNewErrorAlreadyExists() throws Exception {

        CropParametersDTO cropParametersDTO = DefaultMappers.cropParametersMapper.entityToDto(
                cropParametersManager.getAllCropParameters(0).getContent().get(2));
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropParametersDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void addNewMissingName() throws Exception {
        CropParametersDTO cropParametersDTO = GrainParametersDTO.builder()
                .resourceType(ResourceType.GRAIN)
                .pollution(0.95f)
                .density(800)
                .fallingNumber(333)
                .build();


        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropParametersDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    void update() throws Exception {
        CropParameters cropParameters = saveNewCropParameters();
        CropParametersDTO cropParametersDTO = DefaultMappers.cropParametersMapper.entityToDto(cropParameters);
        cropParametersDTO.setName("TEST_UPDATED");
        cropParametersDTO.setComment("DESCRIPTION_UPDATED");
        ((GrainParametersDTO) cropParametersDTO).setDensity(132.11f);
        mockMvc.perform(patch(CropParametersController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropParametersDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        CropParameters cropParametersUpdated = cropParametersManager.getCropParametersById(cropParameters.getId());
        assertThat(cropParametersUpdated).isNotNull();
        assertThat(cropParametersUpdated.getName()).isEqualTo("TEST_UPDATED");
        assertThat(cropParametersUpdated.getComment()).isEqualTo("DESCRIPTION_UPDATED");
        assertThat(((GrainParameters) cropParametersUpdated).getDensity().floatValue()).isEqualTo(
                ((GrainParametersDTO) cropParametersDTO).getDensity());
    }

    private CropParameters saveNewCropParameters() {
        return cropParametersManager.addCropParameters(GrainParametersDTO.builder()
                .name("TEST")
                .resourceType(ResourceType.GRAIN)
                .pollution(0.95f)
                .density(800)
                .fallingNumber(333)
                .build());

    }

    @Test
    @Transactional
    void updateAccessDenied() throws Exception {
        CropParametersDTO cropParametersDTO = DefaultMappers.cropParametersMapper.entityToDto(
                cropParametersManager.getUndefinedCropParameters());
        cropParametersDTO.setName("TEST_UPDATED");
        mockMvc.perform(patch(CropParametersController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropParametersDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void updateModelBlank() throws Exception {
        CropParameters cropParameters = saveNewCropParameters();
        CropParametersDTO cropParametersDTO = DefaultMappers.cropParametersMapper.entityToDto(cropParameters);
        cropParametersDTO.setName("");
        mockMvc.perform(patch(CropParametersController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropParametersDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteCropParameters() throws Exception {
        CropParameters cropParameters = saveNewCropParameters();

        mockMvc.perform(delete(CropParametersController.BASE_PATH)
                        .param("id", cropParameters.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(cropParametersManager.getCropParametersById(cropParameters.getId())).isEqualTo(CropParameters.NONE);
    }

    @Test
    void deleteFailed() throws Exception {
        CropParameters cropParameters = cropParametersManager.getAllCropParameters(0).getContent().getFirst();

        mockMvc.perform(delete(CropParametersController.BASE_PATH)
                        .param("id", cropParameters.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
        assertThat(cropParametersManager.getCropParametersById(cropParameters.getId()).equals(CropParameters.NONE)).isFalse();

    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

