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
    @Column(nullable = false)
    private int id;
    @Column(nullable = false)
    private String name;
    private String value;
    @Column(nullable = false)
    private String serverId;

    public Config() {
        //no-op
    }

    public Config(String serverId, String configName, String configValue) {
        this.serverId = serverId;
        this.name = configName;
        this.value = configValue;
    }
}
