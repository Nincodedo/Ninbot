package dev.nincodedo.ninbot.components.config;

import dev.nincodedo.ninbot.common.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class Config extends BaseEntity {

    @Column(nullable = false)
    private String name;
    private String value;
    private String serverId;
    @Column(columnDefinition = "boolean default false")
    private Boolean global = false;

    public Config() {
        //no-op
    }

    public Config(String serverId, String configName, String configValue) {
        this.serverId = serverId;
        this.name = configName;
        this.value = configValue;
    }
}
