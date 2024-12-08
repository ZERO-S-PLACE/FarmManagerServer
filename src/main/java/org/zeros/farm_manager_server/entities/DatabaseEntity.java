package org.zeros.farm_manager_server.entities;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected UUID id;
    @Version
    protected Integer version;
    @CreationTimestamp
    @Column( updatable = false)
    protected Timestamp dateCreated;
    @UpdateTimestamp
    protected Timestamp dateUpdated;
}
