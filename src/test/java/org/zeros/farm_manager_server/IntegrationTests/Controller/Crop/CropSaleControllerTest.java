package org.zeros.farm_manager_server.IntegrationTests.Controller.Crop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Controllers.Crop.CropSaleController;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropSaleDTO;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropSale;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Services.Default.Crop.CropParametersManagerDefault;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropSaleManager;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserDataReader;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
public class CropSaleControllerTest {
    @Autowired
    CropSaleController cropSaleController;
    @Autowired
    CropSaleManager cropSaleManager;
    @Autowired
    UserManager userManager;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;
    MockMvc mockMvc;

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
    Crop unsoldCrop;
    Crop archivedCrop;
    @Autowired
    private UserDataReader userDataReader;
    @Autowired
    private CropParametersManagerDefault cropParametersManagerDefault;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        User user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
        Field field = user.getFields().stream().findAny().orElse(Field.NONE);
        FieldPart fieldPart = field.getFieldParts().stream().filter(fieldPart1 -> !fieldPart1.getIsArchived()).findAny().orElse(FieldPart.NONE);
        unsoldCrop = userDataReader.getAllUnsoldCrops().stream().findAny().orElse(null);
        archivedCrop = fieldPart.getArchivedCrops().stream().findFirst().orElse(MainCrop.NONE);
    }

    @Test
    @Transactional
    void getById() throws Exception {
        CropSale cropSale = saveNewCropSale();
        MvcResult result = mockMvc.perform(
                        get(CropSaleController.ID_PATH, cropSale.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(cropSale.getId().toString())))
                .andReturn();

        displayResponse(result);
    }

    @Test
    @Transactional
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get(CropSaleController.ID_PATH, UUID.randomUUID().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    @Transactional
    void addNew() throws Exception {
        CropSaleDTO cropSaleDTO = CropSaleDTO.builder()
                .cropParameters(cropParametersManagerDefault.getUndefinedCropParameters().getId())
                .resourceType(ResourceType.GRAIN)
                .amountSold(1)
                .dateSold(LocalDate.now())
                .soldTo("TEST")
                .pricePerUnit(550)
                .build();

        MvcResult result = mockMvc.perform(post(CropSaleController.BASE_PATH)
                        .param("cropId",unsoldCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropSaleDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();
        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[5]);


        CropSale cropSale = cropSaleManager.getCropSaleById(savedUUID);
        assertThat(cropSale).isNotEqualTo(CropSale.NONE);
        assertThat(cropSale.getSoldTo()).isEqualTo(cropSaleDTO.getSoldTo());
        assertThat(cropSale.getResourceType()).isEqualTo(cropSaleDTO.getResourceType());
        displayResponse(result);
    }

    @Test
    @Transactional
    void addNewErrorAlreadyExists() throws Exception {
        CropSaleDTO cropSaleDTO = DefaultMappers.cropSaleMapper.entityToDto(((MainCrop) archivedCrop).getCropSales().stream().findFirst().get());

        mockMvc.perform(post(CropSaleController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropSaleDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @Transactional
    void update() throws Exception {
        CropSale cropSale = saveNewCropSale();
        CropSaleDTO cropSaleDTO = DefaultMappers.cropSaleMapper.entityToDto(cropSale);
        cropSaleDTO.setAmountSold(cropSaleDTO.getAmountSold() * 2);
        mockMvc.perform(patch(CropSaleController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropSaleDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        CropSale cropSaleUpdated = cropSaleManager.getCropSaleById(cropSale.getId());
        assertThat(cropSaleUpdated).isNotNull();
        assertThat(cropSaleUpdated.getAmountSold().floatValue()).isEqualTo(cropSaleDTO.getAmountSold());

    }

    private CropSale saveNewCropSale() {
        return cropSaleManager.addCropSale(unsoldCrop.getId(), CropSaleDTO.builder()
                .cropParameters(cropParametersManagerDefault.getUndefinedCropParameters().getId())
                .resourceType(ResourceType.GRAIN)
                .amountSold(1)
                .crop(unsoldCrop.getId())
                .dateSold(LocalDate.now())
                .soldTo("TEST")
                .pricePerUnit(550)
                .build());

    }


    @Test
    @Transactional
    void deleteCropSale() throws Exception {
        CropSale cropSale = saveNewCropSale();
        mockMvc.perform(delete(CropSaleController.BASE_PATH)
                        .param("id", cropSale.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(cropSaleManager.getCropSaleById(cropSale.getId())).isEqualTo(CropSale.NONE);
    }

    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }

}

