package dev.nincodedo.ninbot.components.config.component;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class DisabledComponents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    private String serverId;
    @ManyToOne
    private Component component;

    public DisabledComponents() {
        //no-op
    }

    DisabledComponents(String serverId, Component component) {
        this.serverId = serverId;
        this.component = component;
    }
}
