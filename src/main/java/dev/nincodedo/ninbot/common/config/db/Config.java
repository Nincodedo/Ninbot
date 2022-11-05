package dev.nincodedo.ninbot.common.config.db;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

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
