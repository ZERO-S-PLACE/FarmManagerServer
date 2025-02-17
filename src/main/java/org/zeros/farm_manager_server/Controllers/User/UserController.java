package org.zeros.farm_manager_server.Controllers.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.Operations.AgriculturalOperation;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.util.UUID;

@Tag(name = "2.User Management", description = "API for managing user information, including user data retrieval and modification.")
@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    public static final String ADMIN_GET_USER_PATH = "/api/admin/user";
    public static final String ADMIN_USER_PATH_ID = ADMIN_GET_USER_PATH + "/{id}";
    public static final String ADMIN_LIST_ALL_USERS_PATH = ADMIN_GET_USER_PATH + "ALL";
    public static final String USER_DATA_PATH = "/api/user/logged_user";
    public static final String REGISTER_PATH = "/api/register";

    private final UserManager userManager;
    private final LoggedUserConfiguration loggedUserConfiguration;

    @Operation(
            summary = "Get current logged-in user data",
            description = "Retrieve the data of the currently logged-in user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user data",
                            content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping(USER_DATA_PATH)
    public UserDTO getCurrentUserData() {
        User user = loggedUserConfiguration.getLoggedUser();
        if (user.getId() == null) {
            throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        return userManager.getUserById(user.getId());
    }

    @Operation(
            summary = "Get user data by ID",
            description = "Retrieve user data by their unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user data",
                            content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping(ADMIN_USER_PATH_ID)
    public UserDTO getUserDataById(@PathVariable("id") UUID id) {
        return userManager.getUserById(id);
    }

    @Operation(
            summary = "Get user data by criteria (email or username)",
            description = "Retrieve user data by either email or username. At least one of these criteria must be provided.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user data",
                            content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid criteria provided"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping(ADMIN_GET_USER_PATH)
    public UserDTO getUserCriteria(@RequestParam(required = false, defaultValue = "") String email,
                                   @RequestParam(required = false, defaultValue = "") String username) {
        UserDTO user;
        if (username.isBlank() && email.isBlank()) {
            throw new IllegalArgumentExceptionCustom(AgriculturalOperation.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        if (email.isBlank()) {
            user = userManager.getUserByUsername(username);
        } else {
            user = userManager.getUserByEmail(email);
        }
        return user;
    }

    @Operation(
            summary = "Get a list of all users",
            description = "Retrieve a paginated list of all users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users",
                            content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid page number provided")
            }
    )
    @GetMapping(ADMIN_LIST_ALL_USERS_PATH)
    public Page<UserDTO> getAllUsers(@RequestParam Integer pageNumber) {
        return userManager.getAllUsers(pageNumber);
    }

    @Operation(
            summary = "Register a new user",
            description = "Register a new user with the provided data.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created the new user"),
                    @ApiResponse(responseCode = "400", description = "Invalid user data provided")
            }
    )
    @PostMapping(REGISTER_PATH)
    ResponseEntity<String> addNew(@RequestBody UserDTO userDTO) {
        UserDTO saved = userManager.registerNewUser(userDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", saved.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update logged-in user data",
            description = "Update the current logged-in user's data.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully updated the user data"),
                    @ApiResponse(responseCode = "400", description = "Invalid user data provided")
            }
    )
    @PatchMapping(USER_DATA_PATH)
    ResponseEntity<String> update(@RequestBody UserDTO userDTO) {
        userManager.updateUserInfo(userDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete current logged-in user",
            description = "Delete the data of the currently logged-in user.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted user data"),
                    @ApiResponse(responseCode = "400", description = "User data could not be deleted")
            }
    )
    @DeleteMapping(USER_DATA_PATH)
    ResponseEntity<String> deleteById() {
        User user = loggedUserConfiguration.getLoggedUser();
        userManager.deleteAllUserData(user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
