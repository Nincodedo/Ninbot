package dev.nincodedo.ninbot.components.config.component;

import javax.persistence.*;

@Entity
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


    public long getId() {
        return this.id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }

    public Component getComponent() {
        return this.component;
    }

    public void setComponent(final Component component) {
        this.component = component;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof DisabledComponents)) return false;
        final DisabledComponents other = (DisabledComponents) o;
        if (!other.canEqual(this)) return false;
        if (this.getId() != other.getId()) return false;
        final java.lang.Object this$serverId = this.getServerId();
        final java.lang.Object other$serverId = other.getServerId();
        if (this$serverId == null ? other$serverId != null : !this$serverId.equals(other$serverId)) return false;
        final java.lang.Object this$component = this.getComponent();
        final java.lang.Object other$component = other.getComponent();
        return this$component == null ? other$component == null : this$component.equals(other$component);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof DisabledComponents;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $id = this.getId();
        result = result * PRIME + (int) ($id >>> 32 ^ $id);
        final java.lang.Object $serverId = this.getServerId();
        result = result * PRIME + ($serverId == null ? 43 : $serverId.hashCode());
        final java.lang.Object $component = this.getComponent();
        result = result * PRIME + ($component == null ? 43 : $component.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "DisabledComponents(id=" + this.getId() + ", serverId=" + this.getServerId() + ", component="
                + this.getComponent() + ")";
    }
}
