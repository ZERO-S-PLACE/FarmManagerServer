package org.zeros.farm_manager_server.IntegrationTests.Controller.User;

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
import org.zeros.farm_manager_server.Controllers.User.UserDataReaderController;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.JWT_Authentication;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@Rollback
public class UserDataReaderControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;
    MockMvc mockMvc;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    void getActiveCrops() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UserDataReaderController.ACTIVE_CROPS_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(Matchers.greaterThan(3))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getUnsoldCrops() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UserDataReaderController.UNSOLD_CROPS_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(Matchers.greaterThan(0))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getPlannedOperations() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UserDataReaderController.PLANNED_OPERATIONS_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(Matchers.greaterThan(3))))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void getPlannedFertilizerApplication() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UserDataReaderController.PLANNED_OPERATIONS_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .param("operationType", String.valueOf(OperationType.FERTILIZER_APPLICATION))
                                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(Matchers.greaterThan(1))))
                .andReturn();
        displayResponse(result);
    }

    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

