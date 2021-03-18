package dev.nincodedo.ninbot.components.ac.turnips;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TurnipPrices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private Long seed;
    @CreatedDate
    private LocalDateTime created;
    @LastModifiedDate
    private LocalDateTime updated;


    public TurnipPrices() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getSeed() {
        return this.seed;
    }

    public void setSeed(final Long seed) {
        this.seed = seed;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return this.updated;
    }

    public void setUpdated(final LocalDateTime updated) {
        this.updated = updated;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof TurnipPrices)) return false;
        final TurnipPrices other = (TurnipPrices) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$seed = this.getSeed();
        final java.lang.Object other$seed = other.getSeed();
        if (this$seed == null ? other$seed != null : !this$seed.equals(other$seed)) return false;
        final java.lang.Object this$created = this.getCreated();
        final java.lang.Object other$created = other.getCreated();
        if (this$created == null ? other$created != null : !this$created.equals(other$created)) return false;
        final java.lang.Object this$updated = this.getUpdated();
        final java.lang.Object other$updated = other.getUpdated();
        return this$updated == null ? other$updated == null : this$updated.equals(other$updated);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof TurnipPrices;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $seed = this.getSeed();
        result = result * PRIME + ($seed == null ? 43 : $seed.hashCode());
        final java.lang.Object $created = this.getCreated();
        result = result * PRIME + ($created == null ? 43 : $created.hashCode());
        final java.lang.Object $updated = this.getUpdated();
        result = result * PRIME + ($updated == null ? 43 : $updated.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "TurnipPrices(id=" + this.getId() + ", seed=" + this.getSeed() + ", created=" + this.getCreated()
                + ", updated=" + this.getUpdated() + ")";
    }
}
