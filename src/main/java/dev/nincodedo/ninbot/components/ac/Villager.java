package dev.nincodedo.ninbot.components.ac;

import javax.persistence.*;

@Entity
public class Villager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String discordId;
    @Column(nullable = false)
    private String discordServerId;
    private int turnipsOwned = 0;
    private int bellsTotal = 2000;

    public Villager() {
    }

    public String getBellsTotalFormatted() {
        return String.format("%,d", getBellsTotal());
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getDiscordId() {
        return this.discordId;
    }

    public void setDiscordId(final String discordId) {
        this.discordId = discordId;
    }

    public String getDiscordServerId() {
        return this.discordServerId;
    }

    public void setDiscordServerId(final String discordServerId) {
        this.discordServerId = discordServerId;
    }

    public int getTurnipsOwned() {
        return this.turnipsOwned;
    }

    public void setTurnipsOwned(final int turnipsOwned) {
        this.turnipsOwned = turnipsOwned;
    }

    public int getBellsTotal() {
        return this.bellsTotal;
    }

    public void setBellsTotal(final int bellsTotal) {
        this.bellsTotal = bellsTotal;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Villager)) return false;
        final Villager other = (Villager) o;
        if (!other.canEqual(this)) return false;
        if (this.getTurnipsOwned() != other.getTurnipsOwned()) return false;
        if (this.getBellsTotal() != other.getBellsTotal()) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$discordId = this.getDiscordId();
        final java.lang.Object other$discordId = other.getDiscordId();
        if (this$discordId == null ? other$discordId != null : !this$discordId.equals(other$discordId)) return false;
        final java.lang.Object this$discordServerId = this.getDiscordServerId();
        final java.lang.Object other$discordServerId = other.getDiscordServerId();
        return this$discordServerId == null ?
                other$discordServerId == null : this$discordServerId.equals(other$discordServerId);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Villager;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getTurnipsOwned();
        result = result * PRIME + this.getBellsTotal();
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $discordId = this.getDiscordId();
        result = result * PRIME + ($discordId == null ? 43 : $discordId.hashCode());
        final java.lang.Object $discordServerId = this.getDiscordServerId();
        result = result * PRIME + ($discordServerId == null ? 43 : $discordServerId.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "Villager(id=" + this.getId() + ", discordId=" + this.getDiscordId() + ", discordServerId="
                + this.getDiscordServerId() + ", turnipsOwned=" + this.getTurnipsOwned() + ", bellsTotal="
                + this.getBellsTotal() + ")";
    }
}
