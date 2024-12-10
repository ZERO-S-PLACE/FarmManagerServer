package org.zeros.farm_manager_server.entities;


import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;
import org.zeros.farm_manager_server.entities.fields.Field;
import org.zeros.farm_manager_server.entities.fields.FieldGroup;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class User extends DatabaseEntity{
    @NotBlank
    @Size(min=2,max=20)
    private String firstName;

    @Size(max=20)
    private String secondName;
    @NotBlank
    @Size(min=2,max=40)
    private String lastName;

    @Email
    @NotBlank
    private String email;
    @Nullable
    @Size(min=2,max=400)
    private String address;
    @Nullable
    @Size(min=2,max=100)
    private String city;
    @Nullable
    @Size(min=2,max=10)
    private String zipCode;
    @Nullable
    @Size(min=2,max=12)
    private String phoneNumber;
    @NotBlank
    @Size(min=2,max=36)
    private String username;
    @NotBlank
    @Size(min=2,max=255)
    private String password;
    @OneToMany(mappedBy = "id")
    private Set<Field> fields=new HashSet<>();
    @OneToMany(mappedBy = "id")
    private Set<FieldGroup> fieldGroups=new HashSet<>();

    public void addField(Field field){
        if(fields==null){
            fields=new HashSet<>();
        }
        if(!field.getUser().equals(this)){
            field.setUser(this);
        }
            fields.add(field);

    }

    public final static User NONE= User.builder()
            .firstName("NONE")
            .lastName("NONE")
            .email("NONE@.com")
            .username("NONE")
            .password("NONE")
            .build();
}
