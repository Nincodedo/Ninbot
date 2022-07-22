package dev.nincodedo.ninbot.common.config.db.component;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

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
