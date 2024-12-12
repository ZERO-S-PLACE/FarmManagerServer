package org.zeros.farm_manager_server.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;


@MappedSuperclass
@Getter
@Setter
public abstract class DatabaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    protected UUID id;
    @Version
    @NotNull
    protected Integer version;
    @CreationTimestamp
    @Column(updatable = false)
    protected Timestamp createdDate;
    @UpdateTimestamp
    protected Timestamp lastModifiedDate;
}
