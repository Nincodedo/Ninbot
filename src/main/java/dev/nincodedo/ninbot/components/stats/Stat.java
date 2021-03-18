package dev.nincodedo.ninbot.components.stats;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String category;
    @Column(nullable = false)
    private String name;
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();
    @LastModifiedDate
    private LocalDateTime modifiedAt;
    private Integer count = 0;
    private String serverId;


    public Stat() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return this.modifiedAt;
    }

    public void setModifiedAt(final LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(final Integer count) {
        this.count = count;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Stat)) return false;
        final Stat other = (Stat) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$count = this.getCount();
        final java.lang.Object other$count = other.getCount();
        if (this$count == null ? other$count != null : !this$count.equals(other$count)) return false;
        final java.lang.Object this$category = this.getCategory();
        final java.lang.Object other$category = other.getCategory();
        if (this$category == null ? other$category != null : !this$category.equals(other$category)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$createdAt = this.getCreatedAt();
        final java.lang.Object other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) return false;
        final java.lang.Object this$modifiedAt = this.getModifiedAt();
        final java.lang.Object other$modifiedAt = other.getModifiedAt();
        if (this$modifiedAt == null ? other$modifiedAt != null : !this$modifiedAt.equals(other$modifiedAt))
            return false;
        final java.lang.Object this$serverId = this.getServerId();
        final java.lang.Object other$serverId = other.getServerId();
        return this$serverId == null ? other$serverId == null : this$serverId.equals(other$serverId);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Stat;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $count = this.getCount();
        result = result * PRIME + ($count == null ? 43 : $count.hashCode());
        final java.lang.Object $category = this.getCategory();
        result = result * PRIME + ($category == null ? 43 : $category.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        final java.lang.Object $modifiedAt = this.getModifiedAt();
        result = result * PRIME + ($modifiedAt == null ? 43 : $modifiedAt.hashCode());
        final java.lang.Object $serverId = this.getServerId();
        result = result * PRIME + ($serverId == null ? 43 : $serverId.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "Stat(id=" + this.getId() + ", category=" + this.getCategory() + ", name=" + this.getName()
                + ", createdAt=" + this.getCreatedAt() + ", modifiedAt=" + this.getModifiedAt() + ", count="
                + this.getCount() + ", serverId=" + this.getServerId() + ")";
    }
}
