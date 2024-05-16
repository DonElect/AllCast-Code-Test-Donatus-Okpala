package com.donatus.simpletaskmanager.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
@EnableJpaAuditing
@Getter
@Setter
public abstract class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = -5554308939380869754L;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime dateModified;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (dateCreated == null) {
            dateCreated = LocalDateTime.now();
        }
        dateModified = LocalDateTime.now();
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
