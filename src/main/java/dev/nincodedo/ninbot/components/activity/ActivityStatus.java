package dev.nincodedo.ninbot.components.activity;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class ActivityStatus extends BaseEntity {
    private String status;
}
