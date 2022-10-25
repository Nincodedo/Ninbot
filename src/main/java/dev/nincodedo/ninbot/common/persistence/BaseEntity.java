package dev.nincodedo.ninbot.common.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    private Long id;
    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    @PostLoad
    private void postLoad() {
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

