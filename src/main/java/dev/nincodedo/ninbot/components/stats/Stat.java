package dev.nincodedo.ninbot.components.stats;


import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
@Data
public class Stat extends BaseEntity {
    private String category;
    @Column(nullable = false)
    private String name;
    private Integer count = 0;
    private String serverId;
}
