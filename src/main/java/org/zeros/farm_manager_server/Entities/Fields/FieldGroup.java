package org.zeros.farm_manager_server.Entities.Fields;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.zeros.farm_manager_server.Entities.DatabaseEntity;
import org.zeros.farm_manager_server.Entities.User.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"fields"})
@Builder
public class FieldGroup extends DatabaseEntity {


    @NotNull
    @NotBlank
    private String fieldGroupName;

    @NonNull
    @Builder.Default
    private String description = "";

    @NotNull
    @OneToMany(mappedBy = "fieldGroup", fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Field> fields = new HashSet<>();

    @NotNull
    @ManyToOne
    private User user;

    @Transient
    public static final FieldGroup NONE = FieldGroup.builder().fieldGroupName("NONE").user(User.NONE).build();

    @Transient
    public static FieldGroup getDefaultFieldGroup(User user) {
        return FieldGroup.builder().fieldGroupName("DEFAULT").user(user).build();
    }


    public void addFields(Set<Field> fields) {
        this.fields.addAll(fields);
    }

    public void addField(Field field) {
        this.fields.add(field);
    }

    public void removeFields(Set<Field> fields) {
        this.fields.removeAll(fields);
    }

    public void removeField(Field field) {
        this.fields.remove(field);
    }

}
