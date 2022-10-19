package dev.nincodedo.ninbot.components.activity;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;
import net.dv8tion.jda.api.entities.Activity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
@Data
public class ActivityStatus extends BaseEntity {
    @Column(nullable = false)
    private String status;
    private Activity.ActivityType activityType;
}
