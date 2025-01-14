package org.zeros.farm_manager_server.IntegrationTests.Controller.Crop;
/*
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
import org.zeros.farm_manager_server.Controllers.Crop.CropController;
import org.zeros.farm_manager_server.Controllers.Data.FarmingMachineController;
import org.zeros.farm_manager_server.Domain.Entities.Crop.Crop;
import org.zeros.farm_manager_server.Domain.Entities.Crop.MainCrop;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldPart;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Enum.ResourceType;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldPartRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Services.Default.User.UserDataReaderDefault;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropManager;
import org.zeros.farm_manager_server.Services.Interface.Fields.FieldPartManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
public class CropControllerTest {
    @Autowired
    CropController cropController;
    @Autowired
    CropManager cropManager;
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
    Crop activeCrop;
    Crop archivedCrop;
    FieldPart fieldPart;
    @Autowired
    private UserDataReaderDefault userDataReaderDefault;

//TODO

    /*
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        User user = userManager.logInNewUserByUsernameAndPassword("DEMO_USER", "DEMO_PASSWORD");
        Field field = user.getFields().stream().findAny().orElse(Field.NONE);
        fieldPart = field.getFieldParts().stream().filter(fieldPart1 -> !fieldPart1.getIsArchived()).findAny().orElse(FieldPart.NONE);
        unsoldCrop = userDataReaderDefault.getAllUnsoldCrops().stream().findAny().orElse(null);
        activeCrop = fieldPart.getActiveCrop();
        archivedCrop = fieldPart.getArchivedCrops().stream().findFirst().orElse(MainCrop.NONE);

    }

//
//    MainCrop createNewMainCrop(@NotNull UUID fieldPartId, @NotNull Set<UUID> cultivatedPlantsIds);
//
//    InterCrop createNewInterCrop(@NotNull UUID fieldPartId, @NotNull Set<UUID> cultivatedPlantsIds);
//
//    void updateCultivatedPlants(@NotNull UUID cropId, @NotNull Set<UUID> cultivatedPlantsIds);
//
//    void addSubside(@NotNull UUID cropId, @NotNull UUID subsideId);
//
//    void removeSubside(@NotNull UUID cropId, @NotNull UUID subsideId);
//
//    void setDateDestroyed(@NotNull UUID interCropId, @NotNull LocalDate dateDestroyed);
//
//    void setWorkFinished(@NotNull UUID cropId);
//
//    void setFullySold(@NotNull UUID mainCropId);
//
//    void deleteCropAndItsData(@NotNull UUID cropId);

    @Test

    void getById() throws Exception {
        Crop crop = fieldPart.getCrops().stream().findAny().orElse(null);
        MvcResult result = mockMvc.perform(get(CropController.ID_PATH, crop.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(crop.getId().toString())))
                .andReturn();

        displayResponse(result);
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get(CropController.ID_PATH, UUID.randomUUID().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test

    void addNewMainCrop() throws Exception {

        MvcResult result = mockMvc.perform(post(CropController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();

        String[] locationUUID = result.getResponse().getHeaders("Location")
                .getFirst().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[5]);

        Crop crop = cropManager.getCropById(savedUUID);
        assertThat(crop).isNotEqualTo(MainCrop.NONE);
        assertThat(crop.getName()).isEqualTo(cropDTO.getName());
        assertThat(crop.getResourceType()).isEqualTo(cropDTO.getResourceType());
        assertThat(((Grain) crop).getDensity().floatValue()).isEqualTo(800);
        displayResponse(result);
    }

    @Test

    void addNewErrorAlreadyExists() throws Exception {

        CropDTO cropDTO = DefaultMappers.cropMapper.entityToDto(
                cropManager.getAllCrop(0).getContent().get(2));
        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test

    void addNewMissingName() throws Exception {
        CropDTO cropDTO = GrainDTO.builder()
                .resourceType(ResourceType.GRAIN)
                .pollution(0.95f)
                .density(800)
                .fallingNumber(333)
                .build();


        mockMvc.perform(post(FarmingMachineController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test

    void update() throws Exception {
        Crop crop = saveNewCrop();
        CropDTO cropDTO = DefaultMappers.cropMapper.entityToDto(crop);
        cropDTO.setName("TEST_UPDATED");
        cropDTO.setComment("DESCRIPTION_UPDATED");
        ((GrainDTO) cropDTO).setDensity(132.11f);
        mockMvc.perform(patch(CropController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        Crop cropUpdated = cropManager.getCropById(crop.getId());
        assertThat(cropUpdated).isNotNull();
        assertThat(cropUpdated.getName()).isEqualTo("TEST_UPDATED");
        assertThat(cropUpdated.getComment()).isEqualTo("DESCRIPTION_UPDATED");
        assertThat(((Grain) cropUpdated).getDensity().floatValue()).isEqualTo(
                ((GrainDTO) cropDTO).getDensity());
    }

    private Crop saveNewCrop() {
        return cropManager.addCrop(GrainDTO.builder()
                .name("TEST")
                .resourceType(ResourceType.GRAIN)
                .pollution(0.95f)
                .density(800)
                .fallingNumber(333)
                .build());

    }

    @Test

    void updateAccessDenied() throws Exception {
        CropDTO cropDTO = DefaultMappers.cropMapper.entityToDto(
                cropManager.getUndefinedCrop());
        cropDTO.setName("TEST_UPDATED");
        mockMvc.perform(patch(CropController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test

    void updateModelBlank() throws Exception {
        Crop crop = saveNewCrop();
        CropDTO cropDTO = DefaultMappers.cropMapper.entityToDto(crop);
        cropDTO.setName("");
        mockMvc.perform(patch(CropController.BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cropDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test

    void deleteCrop() throws Exception {
        Crop crop = saveNewCrop();

        mockMvc.perform(delete(CropController.BASE_PATH)
                        .param("id", crop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(cropManager.getCropById(crop.getId())).isEqualTo(Crop.NONE);
    }

    @Test
    void deleteFailed() throws Exception {
        Crop crop = cropManager.getAllCrop(0).getContent().getFirst();

        mockMvc.perform(delete(CropController.BASE_PATH)
                        .param("id", crop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
        assertThat(cropManager.getCropById(crop.getId()).equals(Crop.NONE)).isFalse();

    }

    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

*/