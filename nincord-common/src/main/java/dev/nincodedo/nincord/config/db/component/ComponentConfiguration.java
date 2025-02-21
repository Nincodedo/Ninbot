package dev.nincodedo.nincord.config.db.component;

import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class ComponentConfiguration extends BaseEntity {
    @Column(nullable = false)
    private String entityId;
    @Column(nullable = false)
    private DiscordEntityType entityType;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean disabled = true;
    @ManyToOne
    private Component component;
}
