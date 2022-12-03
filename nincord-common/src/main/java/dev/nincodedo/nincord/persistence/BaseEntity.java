package dev.nincodedo.nincord.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @EqualsAndHashCode.Exclude
    private Long id;
    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    @PostLoad
    protected void postLoad() {
        if (audit == null) {
            audit = new AuditMetadata();
        }
    }

    @PrePersist
    private void prePersist() {
        audit.setCreatedDateTime(LocalDateTime.now());
    }

    @PreUpdate
    private void preUpdate() {
        audit.setModifiedDateTime(LocalDateTime.now());
    }
}
