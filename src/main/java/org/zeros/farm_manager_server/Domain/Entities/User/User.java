package org.zeros.farm_manager_server.Domain.Entities.User;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.Fields.Field;
import org.zeros.farm_manager_server.Domain.Entities.Fields.FieldGroup;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"fields", "fieldGroups"})
@SuperBuilder
public class User extends BaseEntity {
    @Transient
    public final static User NONE = User.builder()
            .firstName("NONE")
            .lastName("NONE")
            .email("NONE@.com")
            .username("NONE")
            .build();

    @NonNull
    @NotBlank
    @Size(min = 2, max = 20)
    @Builder.Default
    private String firstName = "";

    @NonNull
    @Builder.Default
    @Size(max = 20)
    private String secondName = "";

    @NonNull
    @NotBlank
    @Size(min = 2, max = 40)
    @Builder.Default
    private String lastName = "";

    @NonNull
    @Email
    @NotBlank
    private String email;

    @NonNull
    @Builder.Default
    @Size(max = 400)
    private String address = "";

    @NonNull
    @Builder.Default
    @Size(max = 100)
    private String city = "";

    @NonNull
    @Builder.Default
    @Size(max = 10)
    private String zipCode = "";

    @NonNull
    @Builder.Default
    @Size(max = 12)
    private String phoneNumber = "";

    @NonNull
    @NotBlank
    @Size(min = 2, max = 36)
    private String username;


    @NonNull
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<Field> fields = new HashSet<>();

    @NonNull
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<FieldGroup> fieldGroups = new HashSet<>();


    public void addField(Field field) {
        if (!field.getUser().equals(this)) {
            field.setUser(this);
        }
        fields.add(field);
    }

    public void addFieldGroup(FieldGroup fieldGroup) {
        if (!fieldGroup.getUser().equals(this)) {
            fieldGroup.setUser(this);
        }
        fieldGroups.add(fieldGroup);
    }
}
