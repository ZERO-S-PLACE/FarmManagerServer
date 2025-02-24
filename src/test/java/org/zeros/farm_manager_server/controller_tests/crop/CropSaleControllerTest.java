package org.zeros.farm_manager_server.controller_tests.crop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.zeros.farm_manager_server.controllers.crop.CropSaleController;
import org.zeros.farm_manager_server.domain.dto.crop.CropSaleDTO;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;
import org.zeros.farm_manager_server.domain.entities.crop.CropSale;
import org.zeros.farm_manager_server.domain.entities.crop.MainCrop;
import org.zeros.farm_manager_server.domain.entities.fields.Field;
import org.zeros.farm_manager_server.domain.entities.fields.FieldPart;
import org.zeros.farm_manager_server.domain.entities.user.User;
import org.zeros.farm_manager_server.domain.enums.ResourceType;
import org.zeros.farm_manager_server.domain.mappers.DefaultMappers;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.repositories.user.UserRepository;
import org.zeros.farm_manager_server.services.default_impl.crop.CropParametersManagerDefault;
import org.zeros.farm_manager_server.services.interfaces.crop.CropSaleManager;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class CropSaleControllerTest {

    @Autowired
    CropSaleManager cropSaleManager;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    Crop unsoldCrop;
    Crop archivedCrop;
    @Autowired
    private CropParametersManagerDefault cropParametersManagerDefault;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        User user = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        Field field = user.getFields().stream().findFirst().orElse(Field.NONE);
        FieldPart fieldPart = field.getFieldParts().stream().filter(fieldPart1 -> !fieldPart1.getIsArchived()).findFirst().orElse(FieldPart.NONE);
        unsoldCrop = fieldPart.getCrops().stream().filter(crop ->
                (crop instanceof MainCrop && !((MainCrop) crop).getIsFullySold() && crop
                        .getWorkFinished())).findAny().orElse(null);
        archivedCrop = fieldPart.getArchivedCrops().stream().findFirst().orElse(MainCrop.NONE);
    }

    @Test
    void getById() throws Exception {
        CropSaleDTO cropSale = saveNewCropSale();
        MvcResult result = mockMvc.perform(
                        get(CropSaleController.ID_PATH, cropSale.getId())
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(cropSale.getId().toString())))
                .andReturn();

        displayResponse(result);
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get(CropSaleController.ID_PATH, UUID.randomUUID().toString())
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void addNew() throws Exception {
        CropSaleDTO cropSaleDTO = CropSaleDTO.builder()
                .cropParameters(cropParametersManagerDefault.getUndefinedCropParameters().getId())
                .resourceType(ResourceType.GRAIN)
                .amountSold(BigDecimal.valueOf(1))
                .dateSold(LocalDate.now())
                .soldTo("TEST")
                .pricePerUnit(BigDecimal.valueOf(550))
                .build();

        MvcResult result = mockMvc.perform(post(CropSaleController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("cropId", unsoldCrop.getId().toString())
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


        CropSaleDTO cropSale = cropSaleManager.getCropSaleById(savedUUID);
        assertThat(cropSale).isNotEqualTo(CropSale.NONE);
        assertThat(cropSale.getSoldTo()).isEqualTo(cropSaleDTO.getSoldTo());
        assertThat(cropSale.getResourceType()).isEqualTo(cropSaleDTO.getResourceType());
        displayResponse(result);
    }

    @Test
    void addNewErrorAlreadyExists() throws Exception {
        CropSaleDTO cropSaleDTO = DefaultMappers.cropSaleMapper.entityToDto(((MainCrop) archivedCrop).getCropSales().stream().findFirst().get());

        mockMvc.perform(post(CropSaleController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropSaleDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    void update() throws Exception {
        CropSaleDTO cropSaleDTO = saveNewCropSale();
        cropSaleDTO.setAmountSold(cropSaleDTO.getAmountSold().multiply(BigDecimal.TWO));
        mockMvc.perform(patch(CropSaleController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropSaleDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        CropSaleDTO cropSaleUpdated = cropSaleManager.getCropSaleById(cropSaleDTO.getId());
        assertThat(cropSaleUpdated).isNotNull();
        assertThat(cropSaleUpdated.getAmountSold().floatValue()).isEqualTo(cropSaleDTO.getAmountSold().floatValue());

    }

    private CropSaleDTO saveNewCropSale() throws Exception {
        CropSaleDTO cropSaleDTO = CropSaleDTO.builder()
                .cropParameters(cropParametersManagerDefault.getUndefinedCropParameters().getId())
                .resourceType(ResourceType.GRAIN)
                .amountSold(BigDecimal.valueOf(1))
                .crop(unsoldCrop.getId())
                .dateSold(LocalDate.now())
                .soldTo("TEST")
                .pricePerUnit(BigDecimal.valueOf(550))
                .build();

        MvcResult result = mockMvc.perform(post(CropSaleController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("cropId", unsoldCrop.getId().toString())
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

        return cropSaleManager.getCropSaleById(savedUUID);
    }


    @Test
    void deleteCropSale() throws Exception {
        CropSaleDTO cropSale = saveNewCropSale();
        mockMvc.perform(delete(CropSaleController.BASE_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .param("id", cropSale.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(IllegalArgumentException.class, () -> cropSaleManager.getCropSaleById(cropSale.getId()));
    }

    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }

}

