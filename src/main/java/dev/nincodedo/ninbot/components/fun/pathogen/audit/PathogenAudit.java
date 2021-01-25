package dev.nincodedo.ninbot.components.fun.pathogen.audit;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class PathogenAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String action;
    private String description;
    @CreatedDate
    private LocalDateTime creationDate;
    @CreatedBy
    private String createdBy;
    private Integer weekId;
}
