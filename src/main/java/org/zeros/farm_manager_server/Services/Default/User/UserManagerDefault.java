package org.zeros.farm_manager_server.Services.Default.User;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zeros.farm_manager_server.Domain.DTO.User.UserDTO;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Domain.Mappers.DefaultMappers;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;
import org.zeros.farm_manager_server.Model.ApplicationDefaults;
import org.zeros.farm_manager_server.Repositories.Data.*;
import org.zeros.farm_manager_server.Repositories.Fields.FieldGroupRepository;
import org.zeros.farm_manager_server.Repositories.Fields.FieldRepository;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.User.UserManager;

import java.util.Set;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class UserManagerDefault implements UserManager {

    private final UserRepository userRepository;
    private final FieldGroupRepository fieldGroupRepository;
    private final FarmingMachineRepository farmingMachineRepository;
    private final SubsideRepository subsideRepository;
    private final PlantRepository plantRepository;
    private final SpeciesRepository speciesRepository;
    private final SprayRepository sprayRepository;
    private final FertilizerRepository fertilizerRepository;
    private final FieldRepository fieldRepository;


    @Override
    @Transactional
    public UserDTO registerNewUser(UserDTO userDTO) {
        if (userDTO.getFirstName() == null || userDTO.getFirstName().isBlank()) {
            throw new IllegalArgumentExceptionCustom(User.class, Set.of("firstName"), IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        if (userDTO.getLastName() == null || userDTO.getLastName().isBlank()) {
            throw new IllegalArgumentExceptionCustom(User.class, Set.of("lastName"), IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
            throw new IllegalArgumentExceptionCustom(User.class, Set.of("email"), IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            throw new IllegalArgumentExceptionCustom(User.class, Set.of("password"), IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        if (userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
            throw new IllegalArgumentExceptionCustom(User.class, Set.of("username"), IllegalArgumentExceptionCause.BLANK_REQUIRED_FIELDS);
        }
        if (userRepository.findUserByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentExceptionCustom(User.class, Set.of("email"), IllegalArgumentExceptionCause.OBJECT_EXISTS);
        }
        if (userRepository.findUserByUsername(userDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentExceptionCustom(User.class, Set.of("username"), IllegalArgumentExceptionCause.OBJECT_EXISTS);
        }

        User userSaved = userRepository.saveAndFlush(rewriteToEntity(userDTO, User.NONE));
        FieldGroup defaultFieldGroup = FieldGroup.getDefaultFieldGroup(userSaved);
        userSaved.addFieldGroup(defaultFieldGroup);
        fieldGroupRepository.saveAndFlush(defaultFieldGroup);
        return DefaultMappers.userMapper.entityToDto(userRepository.saveAndFlush(userSaved));

    }

    private User rewriteToEntity(UserDTO dto, User entity) {
        User entityParsed = DefaultMappers.userMapper.dtoToEntitySimpleProperties(dto);
        entityParsed.setVersion(entity.getVersion());
        entityParsed.setCreatedDate(entity.getCreatedDate());
        entityParsed.setLastModifiedDate(entity.getLastModifiedDate());
        return entityParsed;
    }

    @Override
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentExceptionCustom(User.class,
                IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
        return DefaultMappers.userMapper.entityToDto(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new IllegalArgumentExceptionCustom(User.class,
                IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
        return DefaultMappers.userMapper.entityToDto(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new IllegalArgumentExceptionCustom(User.class,
                IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST));
        return DefaultMappers.userMapper.entityToDto(user);
    }

    @Override
    public Page<UserDTO> getAllUsers(int pageNumber) {
        return userRepository.findAll(PageRequest.of(pageNumber, ApplicationDefaults.pageSize,
                Sort.by("username"))).map(DefaultMappers.userMapper::entityToDto);
    }


    @Override
    public UserDTO updateUserInfo(UserDTO userDTO) {
        User savedUser = userRepository.findUserById(userDTO.getId()).orElse(User.NONE);
        if (savedUser.equals(User.NONE)) {
            throw new IllegalArgumentExceptionCustom(User.class, IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST);
        }
        if (!(userDTO.getFirstName() == null || userDTO.getFirstName().isBlank())) {
            savedUser.setFirstName(userDTO.getFirstName());
        }
        if (!(userDTO.getSecondName() == null || userDTO.getSecondName().isBlank())) {
            savedUser.setSecondName(userDTO.getSecondName());
        }
        if (!(userDTO.getLastName() == null || userDTO.getLastName().isBlank())) {
            savedUser.setLastName(userDTO.getLastName());
        }

        return getUserById(savedUser.getId());
    }

    @Override
    public void deleteAllUserData(UUID userId) {
        User user = userRepository.findById(userId).orElse(User.NONE);
        if (user == User.NONE) {
            return;
        }
        fieldGroupRepository.deleteAll(user.getFieldGroups());
        fieldRepository.deleteAll(user.getFields());
        farmingMachineRepository.deleteAllByCreatedBy(user.getUsername());
        subsideRepository.deleteAllByCreatedBy(user.getUsername());
        plantRepository.deleteAllByCreatedBy(user.getUsername());
        speciesRepository.deleteAllByCreatedBy(user.getUsername());
        sprayRepository.deleteAllByCreatedBy(user.getUsername());
        fertilizerRepository.deleteAllByCreatedBy(user.getUsername());
        userRepository.delete(user);
    }
}
