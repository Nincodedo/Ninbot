package com.nincraft.ninbot.components.config;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class Config implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
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

    public Config(String configName, String configValue) {
        this.name = configName;
        this.value = configValue;
        this.global = true;
    }
}
