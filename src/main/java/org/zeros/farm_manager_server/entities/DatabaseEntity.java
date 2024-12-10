package org.zeros.farm_manager_server.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;
import java.util.Timer;
import java.util.UUID;


@MappedSuperclass
@Getter
@Setter
public abstract class DatabaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)", unique = true, nullable = false)
    protected UUID id;
    @Version
    @NotNull
    protected Integer version;
    @CreationTimestamp
    @NotNull
    @Column(name="created_date",updatable = false)
    protected Timestamp createdDate;
    @UpdateTimestamp
    @NotNull
    @Column(name="created_date",updatable = false)
    protected Timestamp lastModifiedDate;
}
