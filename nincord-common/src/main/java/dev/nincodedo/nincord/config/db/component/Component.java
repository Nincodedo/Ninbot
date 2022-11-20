package dev.nincodedo.nincord.config.db.component;

import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.Data;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
@Data
public class Component extends BaseEntity {

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @ToString.Exclude
    private ComponentType type;

    public Component() {
        //no-op
    }

    Component(String name, ComponentType type) {
        this.name = name;
        this.type = type;
    }
}
