package org.zeros.farm_manager_server.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zeros.farm_manager_server.Controllers.UserController;
import org.zeros.farm_manager_server.Entities.User.User;
import org.zeros.farm_manager_server.Repositories.UserRepository;
import org.zeros.farm_manager_server.Services.Default.UserManagerDefault;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    UserController userController;

    @Autowired
    UserManagerDefault userManager;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }





    @Test
    void testGetUserById() throws Exception {
        User user = userManager.getUserByUsername("DEMO_USER");
        MvcResult result= mockMvc.perform(get(UserController.USER_PATH)
                        .queryParam("userId", user.getId().toString()))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.content.size()", is(336)))
                //.andExpect(jsonPath("$.content.id", is(user.getId().toString())))
                //.andExpect(jsonPath("$.content.username", is(user.getUsername())))
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }
}

