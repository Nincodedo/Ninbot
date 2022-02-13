package dev.nincodedo.ninbot.components.config.component;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Component {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private long id;
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
