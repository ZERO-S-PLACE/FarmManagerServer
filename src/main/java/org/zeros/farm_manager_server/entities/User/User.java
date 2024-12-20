package org.zeros.farm_manager_server.entities.User;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.DatabaseEntity;
import org.zeros.farm_manager_server.entities.fields.Field;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"loginError", "userCreationError", "fields", "fieldGroups"})
@Builder
public class User extends DatabaseEntity {
    @Transient
    public final static User NONE = User.builder().firstName("NONE").lastName("NONE").email("NONE@.com").username("NONE").password("NONE").build();
    @NotBlank
    @Size(min = 2, max = 20)
    @Builder.Default
    private String firstName = "";
    @Size(max = 20)
    private String secondName;
    @NotBlank
    @Size(min = 2, max = 40)
    @Builder.Default
    private String lastName = "";
    @Email
    @NotBlank
    @Builder.Default
    private String email = "";
    @Nullable
    @Size(min = 2, max = 400)
    private String address;
    @Nullable
    @Size(min = 2, max = 100)
    private String city;
    @Nullable
    @Size(min = 2, max = 10)
    private String zipCode;
    @Nullable
    @Size(min = 2, max = 12)
    private String phoneNumber;
    @NotBlank
    @Size(min = 2, max = 36)
    @Builder.Default
    private String username = "";
    @NotBlank
    @Size(min = 2, max = 255)
    @Builder.Default
    private String password = "";
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Field> fields;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<FieldGroup> fieldGroups;

    @Transient
    private LoginError loginError;
    @Transient
    private UserCreationError userCreationError;

    public static User getBlankUserWithError(UserCreationError userCreationError) {
        User user = NONE;
        user.setUserCreationError(userCreationError);
        return user;
    }

    public static User getBlankUserWithError(LoginError loginError) {
        User user = NONE;
        user.setLoginError(loginError);
        return user;
    }

    public void addField(Field field) {
        if (fields == null) {
            fields = new HashSet<>();
        }
        if (!field.getUser().equals(this)) {
            field.setUser(this);
        }
        fields.add(field);
    }

    public void addFieldGroup(FieldGroup fieldGroup) {
        if (fieldGroups == null) {
            fieldGroups = new HashSet<>();
        }
        if (!fieldGroup.getUser().equals(this)) {
            fieldGroup.setUser(this);
        }
        fieldGroups.add(fieldGroup);
    }
}
