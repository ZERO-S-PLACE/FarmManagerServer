package org.zeros.farm_manager_server.Services.Default;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.User.LoginError;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Entities.User.UserCreationError;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Repositories.Data.*;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.UserManager;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class UserManagerDefault implements UserManager {

    private final UserRepository userRepository;
    private final LoggedUserConfiguration loggedUserConfiguration;
    private final FieldGroupRepository fieldGroupRepository;
    private final FarmingMachineRepository farmingMachineRepository;
    private final SubsideRepository subsideRepository;
    private final PlantRepository plantRepository;
    private final SpeciesRepository speciesRepository;
    private final SprayRepository sprayRepository;
    private final FertilizerRepository fertilizerRepository;


    @Override
    public User createNewUser(UserDTO userDTO) {
        if (userDTO.getFirstName().isBlank()) {
            return User.getBlankUserWithError(UserCreationError.FIRST_NAME_MISSING);
        }
        if (userDTO.getLastName().isBlank()) {
            return User.getBlankUserWithError(UserCreationError.LAST_NAME_MISSING);
        }
        if (userDTO.getEmail().isBlank()) {
            return User.getBlankUserWithError(UserCreationError.EMAIL_MISSING);
        }
        if (userDTO.getPassword().isBlank()) {
            return User.getBlankUserWithError(UserCreationError.PASSWORD_MISSING);
        }
        if (userDTO.getUsername().isBlank()) {
            return User.getBlankUserWithError(UserCreationError.USERNAME_MISSING);
        }
        if (userRepository.findUserByEmail(userDTO.getEmail()).isPresent()) {
            return User.getBlankUserWithError(UserCreationError.EMAIL_NOT_UNIQUE);
        }
        if (userRepository.findUserByUsername(userDTO.getUsername()).isPresent()) {
            return User.getBlankUserWithError(UserCreationError.USERNAME_NOT_UNIQUE);
        }
        try {
            User userSaved = userRepository.saveAndFlush(rewriteToEntity(userDTO, User.NONE));
            FieldGroup defaultFieldGroup = FieldGroup.getDefaultFieldGroup(userSaved);
            userSaved.addFieldGroup(defaultFieldGroup);
            fieldGroupRepository.saveAndFlush(defaultFieldGroup);
            return userRepository.saveAndFlush(userSaved);
        } catch (Exception e) {
            return User.getBlankUserWithError(UserCreationError.UNKNOWN);
        }
    }

    private User rewriteToEntity(UserDTO dto, User entity) {
        User entityParsed = DefaultMappers.userMapper.dtoToEntitySimpleProperties(dto);
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        return entityParsed;
    }

    @Override
    public User getUserById(UUID id) {

        return userRepository.findUserById(id).orElse(User.NONE);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElse(User.NONE);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElse(User.NONE);
    }

    @Override
    public User logInNewUserByEmailAndPassword(String email, String password) {
        User newUser = userRepository.findUserByEmail(email).orElse(User.NONE);
        if (newUser.equals(User.NONE)) {
            return User.getBlankUserWithError(LoginError.WRONG_EMAIL);
        }
        if (!newUser.getPassword().equals(password)) {
            return User.getBlankUserWithError(LoginError.WRONG_PASSWORD);
        }
        loggedUserConfiguration.replaceUserBean(newUser);

        return newUser;
    }

    @Override
    public User logInNewUserByUsernameAndPassword(String username, String password) {
        User newUser = userRepository.findUserByUsername(username).orElse(User.NONE);
        if (newUser.equals(User.NONE)) {
            return User.getBlankUserWithError(LoginError.WRONG_USERNAME);
        }
        if (!newUser.getPassword().equals(password)) {
            return User.getBlankUserWithError(LoginError.WRONG_PASSWORD);
        }
        loggedUserConfiguration.replaceUserBean(newUser);
        return newUser;
    }

    @Override
    public void logOutUser() {
        loggedUserConfiguration.replaceUserBean(User.NONE);
    }

    @Override
    public User updateUserInfo(UserDTO userDTO) {
        User savedUser = userRepository.findUserById(userDTO.getId()).orElse(User.NONE);
        if (savedUser.equals(User.NONE)) {
            return User.NONE;
        }
        if (!userDTO.getFirstName().isBlank()) {
            savedUser.setFirstName(userDTO.getFirstName());
        }
        if (!userDTO.getSecondName().isBlank()) {
            savedUser.setSecondName(userDTO.getSecondName());
        }
        if (!userDTO.getLastName().isBlank()) {
            savedUser.setLastName(userDTO.getLastName());
        }
        if (!userDTO.getPassword().isBlank()) {
            savedUser.setPassword(userDTO.getPassword());
        }
        try {
            return userRepository.saveAndFlush(savedUser);
        } catch (Exception e) {
            return User.getBlankUserWithError(UserCreationError.UNKNOWN);
        }
    }

    @Override
    public void deleteAllUserData(UUID userId) {
        User user = getUserById(userId);
        if (user == User.NONE) {
            return;
        }
        fieldGroupRepository.deleteAll(user.getFieldGroups());
        farmingMachineRepository.deleteAllByCreatedBy(user.getUsername());
        subsideRepository.deleteAllByCreatedBy(user.getUsername());
        plantRepository.deleteAllByCreatedBy(user.getUsername());
        speciesRepository.deleteAllByCreatedBy(user.getUsername());
        sprayRepository.deleteAllByCreatedBy(user.getUsername());
        fertilizerRepository.deleteAllByCreatedBy(user.getUsername());
        userRepository.delete(user);
    }
}
