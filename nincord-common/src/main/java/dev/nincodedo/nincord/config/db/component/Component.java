package dev.nincodedo.nincord.config.db.component;

import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class Component extends BaseEntity {

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @ToString.Exclude
    private ComponentType type;

    Component(String name, ComponentType type) {
        this.name = name;
        this.type = type;
    }
}
