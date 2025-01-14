package org.zeros.farm_manager_server.IntegrationTests.Controller.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.zeros.farm_manager_server.Controllers.Data.FarmingMachineController;
import org.zeros.farm_manager_server.Controllers.User.UserController;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.JWT_Authentication;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.Data.SpeciesManager;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;


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
public class UserControllerTest {

    @Autowired
    UserManager userManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;
    MockMvc mockMvc;

    private User demoUser;

    //ToDo add remaining tests

    @BeforeEach
    void setUp() {
        demoUser = userRepository.findUserById(JWT_Authentication.USER_ID).orElseThrow();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    void getUserInfo() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UserController.USER_DATA_PATH)
                                .with(JWT_Authentication.jwtRequestPostProcessor)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(demoUser.getId().toString())))
                .andExpect(jsonPath("$.username", is(demoUser.getUsername())))
                .andReturn();
        displayResponse(result);
    }

    @Test
    void registerNewUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .firstName("TEST")
                .lastName("TEST")
                .email("TEST2@zeros.org")
                .username("TEST2")
                .password("password")
                .build();

        MvcResult result = mockMvc.perform(post(UserController.REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getHeaders("Location")).isNotNull();
        UUID savedUUID = UUID.fromString(result.getResponse().getHeaders("Location").getFirst());

        assertThat(userManager.getUserById(savedUUID).getFirstName()).isEqualTo(userDTO.getFirstName());
        displayResponse(result);
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete(UserController.USER_DATA_PATH)
                        .with(JWT_Authentication.jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThat(userRepository.findById(demoUser.getId()).isPresent()).isFalse();
    }


    private void displayResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = result.getResponse().getContentAsString();
        String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper.readTree(responseContent));
        System.out.println(formattedResponse);
    }


}

