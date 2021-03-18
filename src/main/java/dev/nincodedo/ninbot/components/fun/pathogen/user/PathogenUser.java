package dev.nincodedo.ninbot.components.fun.pathogen.user;

import javax.persistence.*;
import java.util.Date;

@Entity
public class PathogenUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String serverId;
    private Integer infectionLevel = 0;
    private Date lastInitialInfection;
    private Date lastInitialCure;


    public PathogenUser() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }

    public Integer getInfectionLevel() {
        return this.infectionLevel;
    }

    public void setInfectionLevel(final Integer infectionLevel) {
        this.infectionLevel = infectionLevel;
    }

    public Date getLastInitialInfection() {
        return this.lastInitialInfection;
    }

    public void setLastInitialInfection(final Date lastInitialInfection) {
        this.lastInitialInfection = lastInitialInfection;
    }

    public Date getLastInitialCure() {
        return this.lastInitialCure;
    }

    public void setLastInitialCure(final Date lastInitialCure) {
        this.lastInitialCure = lastInitialCure;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof PathogenUser)) return false;
        final PathogenUser other = (PathogenUser) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$infectionLevel = this.getInfectionLevel();
        final java.lang.Object other$infectionLevel = other.getInfectionLevel();
        if (this$infectionLevel == null ?
                other$infectionLevel != null : !this$infectionLevel.equals(other$infectionLevel)) return false;
        final java.lang.Object this$userId = this.getUserId();
        final java.lang.Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final java.lang.Object this$serverId = this.getServerId();
        final java.lang.Object other$serverId = other.getServerId();
        if (this$serverId == null ? other$serverId != null : !this$serverId.equals(other$serverId)) return false;
        final java.lang.Object this$lastInitialInfection = this.getLastInitialInfection();
        final java.lang.Object other$lastInitialInfection = other.getLastInitialInfection();
        if (this$lastInitialInfection == null ?
                other$lastInitialInfection != null : !this$lastInitialInfection.equals(other$lastInitialInfection))
            return false;
        final java.lang.Object this$lastInitialCure = this.getLastInitialCure();
        final java.lang.Object other$lastInitialCure = other.getLastInitialCure();
        return this$lastInitialCure == null ?
                other$lastInitialCure == null : this$lastInitialCure.equals(other$lastInitialCure);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof PathogenUser;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $infectionLevel = this.getInfectionLevel();
        result = result * PRIME + ($infectionLevel == null ? 43 : $infectionLevel.hashCode());
        final java.lang.Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final java.lang.Object $serverId = this.getServerId();
        result = result * PRIME + ($serverId == null ? 43 : $serverId.hashCode());
        final java.lang.Object $lastInitialInfection = this.getLastInitialInfection();
        result = result * PRIME + ($lastInitialInfection == null ? 43 : $lastInitialInfection.hashCode());
        final java.lang.Object $lastInitialCure = this.getLastInitialCure();
        result = result * PRIME + ($lastInitialCure == null ? 43 : $lastInitialCure.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "PathogenUser(id=" + this.getId() + ", userId=" + this.getUserId() + ", serverId=" + this.getServerId()
                + ", infectionLevel=" + this.getInfectionLevel() + ", lastInitialInfection="
                + this.getLastInitialInfection() + ", lastInitialCure=" + this.getLastInitialCure() + ")";
    }
}
