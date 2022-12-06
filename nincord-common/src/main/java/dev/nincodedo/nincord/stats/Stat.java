package dev.nincodedo.nincord.stats;


import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Data
public class Stat extends BaseEntity {
    private String category;
    @Column(nullable = false)
    private String name;
    private Integer count = 0;
    private String serverId;

    public Stat(String name, String category, String serverId) {
        this.name = name;
        this.category = category;
        this.serverId = serverId;
    }
}
