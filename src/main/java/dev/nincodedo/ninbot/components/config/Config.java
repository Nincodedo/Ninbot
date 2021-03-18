package dev.nincodedo.ninbot.components.config;

import javax.persistence.*;
import java.io.Serializable;

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


    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }

    public Boolean getGlobal() {
        return this.global;
    }

    public void setGlobal(final Boolean global) {
        this.global = global;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Config)) return false;
        final Config other = (Config) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$global = this.getGlobal();
        final java.lang.Object other$global = other.getGlobal();
        if (this$global == null ? other$global != null : !this$global.equals(other$global)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$value = this.getValue();
        final java.lang.Object other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
        final java.lang.Object this$serverId = this.getServerId();
        final java.lang.Object other$serverId = other.getServerId();
        return this$serverId == null ? other$serverId == null : this$serverId.equals(other$serverId);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Config;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $global = this.getGlobal();
        result = result * PRIME + ($global == null ? 43 : $global.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $value = this.getValue();
        result = result * PRIME + ($value == null ? 43 : $value.hashCode());
        final java.lang.Object $serverId = this.getServerId();
        result = result * PRIME + ($serverId == null ? 43 : $serverId.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "Config(id=" + this.getId() + ", name=" + this.getName() + ", value=" + this.getValue() + ", serverId="
                + this.getServerId() + ", global=" + this.getGlobal() + ")";
    }
}
