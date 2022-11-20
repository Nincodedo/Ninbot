package dev.nincodedo.nincord.config.db.component;

import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.Data;
import lombok.ToString;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
@Data
public class DisabledComponents extends BaseEntity {

    private String serverId;
    @ManyToOne
    @ToString.Exclude
    private Component component;

    public DisabledComponents() {
        //no-op
    }

    DisabledComponents(String serverId, Component component) {
        this.serverId = serverId;
        this.component = component;
    }
}
