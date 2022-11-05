package dev.nincodedo.ninbot.common.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class AuditMetadata {
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private LocalDateTime createdDateTime;
    @LastModifiedBy
    private String modifiedBy;
    @LastModifiedDate
    private LocalDateTime modifiedDateTime;

    @PrePersist
    private void prePersist() {
        createdDateTime = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        modifiedDateTime = LocalDateTime.now();
    }

    @Transient
    public void setCreatedModifiedBy(String userId) {
        if (createdBy == null) {
            createdBy = userId;
        } else {
            modifiedBy = userId;
        }
    }
}
