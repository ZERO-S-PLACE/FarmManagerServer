package org.zeros.farm_manager_server.DAO.Default;


import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.UserFieldsManager;
import org.zeros.farm_manager_server.DAO.Interface.UserManager;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.entities.User.LoginError;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.entities.User.UserCreationError;
import org.zeros.farm_manager_server.repositories.Data.*;
import org.zeros.farm_manager_server.repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.repositories.UserRepository;

import java.util.UUID;

@Component
public class UserManagerDefault implements UserManager {

    private final UserRepository userRepository;

    private final LoggedUserConfiguration loggedUserConfiguration;

    private final FieldGroupRepository fieldGroupRepository;
    private final UserFieldsManager userFieldsManager;
    private final FarmingMachineRepository farmingMachineRepository;
    private final SubsideRepository subsideRepository;
    private final PlantRepository plantRepository;
    private final SpeciesRepository speciesRepository;
    private final SprayRepository sprayRepository;
    private final FertilizerRepository fertilizerRepository;

    public UserManagerDefault(UserRepository userRepository,
                              LoggedUserConfiguration loggedUserConfiguration,
                              FieldGroupRepository fieldGroupRepository,
                              UserFieldsManager userFieldsManager, FarmingMachineRepository farmingMachineRepository, SubsideRepository subsideRepository, PlantRepository plantRepository, SpeciesRepository speciesRepository, SprayRepository sprayRepository, FertilizerRepository fertilizerRepository) {
        this.userRepository = userRepository;
        this.loggedUserConfiguration = loggedUserConfiguration;
        this.fieldGroupRepository = fieldGroupRepository;
        this.userFieldsManager = userFieldsManager;
        this.farmingMachineRepository = farmingMachineRepository;
        this.subsideRepository = subsideRepository;
        this.plantRepository = plantRepository;
        this.speciesRepository = speciesRepository;
        this.sprayRepository = sprayRepository;
        this.fertilizerRepository = fertilizerRepository;
    }

    @Override
    public User createNewUser(User user) {
        if (user.getFirstName().isBlank()) {
            return User.getBlankUserWithError(UserCreationError.FIRST_NAME_MISSING);
        }
        if (user.getLastName().isBlank()) {
            return User.getBlankUserWithError(UserCreationError.LAST_NAME_MISSING);
        }
        if (user.getEmail().isBlank()) {
            return User.getBlankUserWithError(UserCreationError.EMAIL_MISSING);
        }
        if (user.getPassword().isBlank()) {
            return User.getBlankUserWithError(UserCreationError.PASSWORD_MISSING);
        }
        if (user.getUsername().isBlank()) {
            return User.getBlankUserWithError(UserCreationError.USERNAME_MISSING);
        }
        if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            return User.getBlankUserWithError(UserCreationError.EMAIL_NOT_UNIQUE);
        }
        if (userRepository.findUserByUsername(user.getUsername()).isPresent()) {
            return User.getBlankUserWithError(UserCreationError.USERNAME_NOT_UNIQUE);
        }
        try {
            User userSaved = userRepository.saveAndFlush(user);
            FieldGroup defaultFieldGroup = FieldGroup.getDefaultFieldGroup(userSaved);
            userSaved.addFieldGroup(defaultFieldGroup);
            fieldGroupRepository.saveAndFlush(defaultFieldGroup);
            return userRepository.saveAndFlush(userSaved);
        } catch (Exception e) {
            return User.getBlankUserWithError(UserCreationError.UNKNOWN);
        }
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
    public User updateUserInfo(User user) {
        User savedUser = userRepository.findUserById(user.getId()).orElse(User.NONE);
        if (savedUser.equals(User.NONE)) {
            return User.NONE;
        }
        if (!user.getFirstName().isBlank()) {
            savedUser.setFirstName(user.getFirstName());
        }
        if (!user.getSecondName().isBlank()) {
            savedUser.setSecondName(user.getSecondName());
        }
        if (!user.getLastName().isBlank()) {
            savedUser.setLastName(user.getLastName());
        }
        if (!user.getPassword().isBlank()) {
            savedUser.setPassword(user.getPassword());
        }
        savedUser.setFields(user.getFields());
        savedUser.setFieldGroups(user.getFieldGroups());
        try {
            return userRepository.saveAndFlush(user);
        } catch (Exception e) {
            return User.getBlankUserWithError(UserCreationError.UNKNOWN);
        }
    }

    @Override
    public void deleteAllUserData(User user) {
        user = getUserById(user.getId());
        for (FieldGroup fieldGroup : user.getFieldGroups()) {
            userFieldsManager.deleteFieldGroupWithFields(fieldGroup);
        }
        farmingMachineRepository.deleteAllByCreatedBy(user.getUsername());
        subsideRepository.deleteAllByCreatedBy(user.getUsername());
        plantRepository.deleteAllByCreatedBy(user.getUsername());
        speciesRepository.deleteAllByCreatedBy(user.getUsername());
        sprayRepository.deleteAllByCreatedBy(user.getUsername());
        fertilizerRepository.deleteAllByCreatedBy(user.getUsername());
        userRepository.delete(user);

    }
}
