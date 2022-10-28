package dev.nincodedo.ninbot.common.config.db.component;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

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
