package org.zeros.farm_manager_server.IntegrationTests.Controller.Crop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Controllers.Crop.CropDataReaderController;
import org.zeros.farm_manager_server.Controllers.Crop.CropParametersController;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.IntegrationTests.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Services.Default.User.UserDataReaderDefault;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
public class CropDataReaderControllerTest {
    @Autowired
    CropParametersController cropParametersController;
    @Autowired
    CropParametersManager cropParametersManager;
    @Autowired
    UserManager userManager;


    @Autowired
    FieldPartManager fieldPartManager;
    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    FieldGroupRepository fieldGroupRepositoryRepository;
    @Autowired
    FieldPartRepository fieldPartRepository;
    @Autowired
    LoggedUserConfiguration loggedUserConfiguration;
    @Autowired
    EntityManager entityManager;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    Crop unsoldCrop;
    Crop activeCrop;
    Crop archivedCrop;





    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        UserDTO user = userManager.getUserByUsername("DEMO_USER");
        UUID fieldId = user.getFields().stream().findAny().orElseThrow();
        Field field=f
        FieldPart fieldPart = field.getFieldParts().stream().filter(fieldPart1 -> !fieldPart1.getIsArchived()).findAny().orElse(FieldPart.NONE);
        unsoldCrop = fieldPart.getCrops().stream().filter(crop ->
            (crop instanceof MainCrop && !((MainCrop) crop).getIsFullySold() && crop
                    .getWorkFinished())
        ).findAny().orElse(null);
        activeCrop = fieldPart.getActiveCrop();
        archivedCrop = fieldPart.getArchivedCrops().stream().findFirst().orElse(MainCrop.NONE);

    }

    @Test

    void getCropSummaryActive() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(CropDataReaderController.CROP_SUMMARY_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .queryParam("cropId", activeCrop.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cropId", is(activeCrop.getId().toString())))
                .andReturn();
        displayResponse(result);
    }

    @Test

    void getCropSummaryUnsold() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(CropDataReaderController.CROP_SUMMARY_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .queryParam("cropId", unsoldCrop.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cropId", is(unsoldCrop.getId().toString())))
                .andReturn();
        displayResponse(result);
    }

    @Test

    void getCropSummaryArchived() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(CropDataReaderController.CROP_SUMMARY_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .queryParam("cropId", archivedCrop.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cropId", is(archivedCrop.getId().toString())))
                .andReturn();
        displayResponse(result);
    }

    @Test

    void getCropSummaryDoesNotExist() throws Exception {
        mockMvc.perform(
                        get(CropDataReaderController.CROP_RESOURCES_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .queryParam("cropId", UUID.randomUUID().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test

    void testGetCropResourcesSummary() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(CropDataReaderController.CROP_RESOURCES_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .queryParam("cropId", archivedCrop.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cropId", is(archivedCrop.getId().toString())))
                .andReturn();
        displayResponse(result);
    }

    @Test

    void getCropResourcesDoesNotExist() throws Exception {
         mockMvc.perform(
                        get(CropDataReaderController.CROP_RESOURCES_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .queryParam("cropId", UUID.randomUUID().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test

    void testGetCropPlannedResourcesSummary() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(CropDataReaderController.CROP_PLANNED_RESOURCES_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .queryParam("cropId", activeCrop.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cropId", is(activeCrop.getId().toString())))
                .andReturn();
        displayResponse(result);
    }

    @Test

    void getCropResourcesPlannedDoesNotExist() throws Exception {
        mockMvc.perform(
                        get(CropDataReaderController.CROP_PLANNED_RESOURCES_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .queryParam("cropId", UUID.randomUUID().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test

    void getMeanCropParameters() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(CropDataReaderController.CROP_MEAN_PARAMETERS)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .queryParam("cropId", archivedCrop.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        displayResponse(result);
    }

    @Test

    void getCropMeanParametersDoesNotExist() throws Exception {
        mockMvc.perform(
                        get(CropDataReaderController.CROP_MEAN_PARAMETERS)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .queryParam("cropId", UUID.randomUUID().toString())
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

