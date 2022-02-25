package dev.nincodedo.ninbot.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    private Long id;
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
