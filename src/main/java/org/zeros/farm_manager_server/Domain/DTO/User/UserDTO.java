package org.zeros.farm_manager_server.Domain.DTO.User;


import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.DTO.BaseEntityDTO;

import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserDTO extends BaseEntityDTO {
    private String firstName;
    private String secondName;
    private String lastName;
    private String email;
    private String address;
    private String city;
    private String zipCode;
    private String phoneNumber;
    private String username;
    private String password;
    private Set<UUID> fields;
    private Set<UUID> fieldGroups;
}
