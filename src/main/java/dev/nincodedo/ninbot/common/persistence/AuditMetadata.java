package dev.nincodedo.ninbot.common.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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
}
