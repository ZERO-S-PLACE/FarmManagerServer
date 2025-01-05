package org.zeros.farm_manager_server.Domain.Entities.Fields;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.zeros.farm_manager_server.Domain.Entities.BaseEntity;
import org.zeros.farm_manager_server.Domain.Entities.User.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"fields"})
@SuperBuilder
public class FieldGroup extends BaseEntity {


    @Transient
    public static final FieldGroup NONE = FieldGroup.builder().fieldGroupName("NONE").user(User.NONE).build();
    @NotNull
    @NotBlank
    private String fieldGroupName;
    @NonNull
    @Builder.Default
    private String description = "";
    @Getter
    @NotNull
    @OneToMany(mappedBy = "fieldGroup")
    @Builder.Default
    private Set<Field> fields = new HashSet<>();
    @NotNull
    @ManyToOne
    private User user;

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
