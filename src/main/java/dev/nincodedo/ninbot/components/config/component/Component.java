package dev.nincodedo.ninbot.components.config.component;

import javax.persistence.*;

@Entity
public class Component {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private ComponentType type;

    public Component() {
        //no-op
    }

    Component(String name, ComponentType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }


    public long getId() {
        return this.id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ComponentType getType() {
        return this.type;
    }

    public void setType(final ComponentType type) {
        this.type = type;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Component)) return false;
        final Component other = (Component) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$type = this.getType();
        final java.lang.Object other$type = other.getType();
        return this$type == null ? other$type == null : this$type.equals(other$type);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Component;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        return result;
    }
}
