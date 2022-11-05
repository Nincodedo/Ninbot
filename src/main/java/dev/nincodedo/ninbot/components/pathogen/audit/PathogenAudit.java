package dev.nincodedo.ninbot.components.pathogen.audit;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;

import jakarta.persistence.Entity;

@Entity
@Data
public class PathogenAudit extends BaseEntity {
    private String action;
    private String description;
    private Integer weekId;
}
