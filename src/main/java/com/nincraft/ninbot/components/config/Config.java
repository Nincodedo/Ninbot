package com.nincraft.ninbot.components.config;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Config")
public class Config implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "Id", nullable = false)
    private int id;
    @Column(name = "Key", nullable = false)
    private String key;
    @Column(name = "Value")
    private String value;
    @Column(name = "ServerId", nullable = false)
    private String serverId;

    public Config() {
        //no-op
    }

    public Config(String serverId, String configName, String configValue) {
        this.serverId = serverId;
        this.key = configName;
        this.value = configValue;
    }
}
