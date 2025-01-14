package org.zeros.farm_manager_server.IntegrationTests.Controller.Crop;

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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zeros.farm_manager_server.Controllers.Crop.CropParametersController;
import org.zeros.farm_manager_server.Controllers.Data.FarmingMachineController;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.GrainParametersDTO;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.GrainParameters;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.IntegrationTests.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.Crop.CropParameters.CropParametersRepository;
import org.zeros.farm_manager_server.Repositories.Crop.CropRepository;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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
    @Autowired
    private CropRepository cropRepository;
    @Autowired
    private CropParametersRepository cropParametersRepository;

    private User user;
    @Autowired
    private UserRepository userRepository;


    @BeforeEach

    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        user =userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
    }

    @Test

    void getById() throws Exception {
        CropParameters cropParameters = findAnyCropParameters();
        MvcResult result = mockMvc.perform(
                        get(CropParametersController.ID_PATH,cropParameters.getId())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
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
        mockMvc.perform(get(CropParametersController.ID_PATH,UUID.randomUUID().toString())
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test

    void getAll() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(CropParametersController.LIST_ALL_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getByNameAs() throws Exception {
        CropParameters cropParameters = findAnyCropParameters();
        MvcResult result = mockMvc.perform(
                        get(CropParametersController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
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
        CropParameters cropParameters = findAnyCropParameters();
        MvcResult result = mockMvc.perform(
                        get(CropParametersController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
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
        CropParameters cropParameters = findAnyCropParameters();
        MvcResult result = mockMvc.perform(
                        get(CropParametersController.LIST_PARAM_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("name", cropParameters.getName().substring(0, 3))
                                .param("resourceType", String.valueOf(cropParameters.getResourceType()))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    private CropParameters findAnyCropParameters() {
        return cropParametersRepository.findAllByCreatedByIn(Set.of("ADMIN",user.getUsername()),
                PageRequest.of(0,5)).getContent().get(1);
    }


    @Test

    void addNew() throws Exception {
        CropParametersDTO cropParametersDTO = GrainParametersDTO.builder()
                .name("TEST")
                .resourceType(ResourceType.GRAIN)
                .pollution(BigDecimal.valueOf(0.95f))
                .density(BigDecimal.valueOf(800))
                .fallingNumber(BigDecimal.valueOf(333))
                .build();

        MvcResult result = mockMvc.perform(post(CropParametersController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropParametersDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[5]);

        CropParameters cropParameters=cropParametersManager.getCropParametersById(savedUUID);
        assertThat(cropParameters).isNotEqualTo(CropParameters.NONE);
        assertThat(cropParameters.getName()).isEqualTo(cropParametersDTO.getName());
        assertThat(cropParameters.getResourceType()).isEqualTo(cropParametersDTO.getResourceType());
        assertThat(((GrainParameters)cropParameters).getDensity().floatValue()).isEqualTo(800);
        displayResponse(result);
    }

    @Test

    void addNewErrorAlreadyExists() throws Exception {

        CropParametersDTO cropParametersDTO = DefaultMappers.cropParametersMapper.entityToDto(findAnyCropParameters());
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropParametersDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test

    void addNewMissingName() throws Exception {
        CropParametersDTO cropParametersDTO = GrainParametersDTO.builder()
                .resourceType(ResourceType.GRAIN)
                .pollution(BigDecimal.valueOf(0.95f))
                .density(BigDecimal.valueOf(800))
                .fallingNumber(BigDecimal.valueOf(333))
                .build();


        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropParametersDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test

    void update() throws Exception {
        CropParameters cropParameters = saveNewCropParameters();
        CropParametersDTO cropParametersDTO = DefaultMappers.cropParametersMapper.entityToDto(cropParameters);
        cropParametersDTO.setName("TEST_UPDATED");
        cropParametersDTO.setComment("DESCRIPTION_UPDATED");
        ((GrainParametersDTO) cropParametersDTO).setDensity(BigDecimal.valueOf(132.11));
        mockMvc.perform(patch(CropParametersController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
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
                ((GrainParametersDTO) cropParametersDTO).getDensity().floatValue());
    }

    private CropParameters saveNewCropParameters() throws Exception {
       CropParametersDTO cropParametersDTO=GrainParametersDTO.builder()
                .name("TEST")
                .resourceType(ResourceType.GRAIN)
                .pollution(BigDecimal.valueOf(0.95))
                .density(BigDecimal.valueOf(800))
                .fallingNumber(BigDecimal.valueOf(333))
                .build();


        MvcResult result = mockMvc.perform(post(CropParametersController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropParametersDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[5]);

      return cropParametersManager.getCropParametersById(savedUUID);
    }

    @Test

    void updateAccessDenied() throws Exception {
        CropParametersDTO cropParametersDTO = DefaultMappers.cropParametersMapper.entityToDto(
                cropParametersManager.getUndefinedCropParameters());
        cropParametersDTO.setName("TEST_UPDATED");
        mockMvc.perform(patch(CropParametersController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropParametersDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test

    void updateModelBlank() throws Exception {
        CropParameters cropParameters = saveNewCropParameters();
        CropParametersDTO cropParametersDTO = DefaultMappers.cropParametersMapper.entityToDto(cropParameters);
        cropParametersDTO.setName("");
        mockMvc.perform(patch(CropParametersController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropParametersDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test

    void deleteCropParameters() throws Exception {
        CropParameters cropParameters = saveNewCropParameters();

        mockMvc.perform(delete(CropParametersController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", cropParameters.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(cropParametersManager.getCropParametersById(cropParameters.getId())).isEqualTo(CropParameters.NONE);
    }

    @Test
    void deleteFailed() throws Exception {
        CropParameters cropParameters = findAnyCropParameters();

        mockMvc.perform(delete(CropParametersController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
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

