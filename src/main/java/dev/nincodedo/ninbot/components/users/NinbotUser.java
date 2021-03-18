package dev.nincodedo.ninbot.components.users;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class NinbotUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String serverId;
    private String birthday;
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();
    @LastModifiedDate
    private LocalDateTime modifiedAt;


    public NinbotUser() {
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

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(final String birthday) {
        this.birthday = birthday;
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

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof NinbotUser)) return false;
        final NinbotUser other = (NinbotUser) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$userId = this.getUserId();
        final java.lang.Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final java.lang.Object this$serverId = this.getServerId();
        final java.lang.Object other$serverId = other.getServerId();
        if (this$serverId == null ? other$serverId != null : !this$serverId.equals(other$serverId)) return false;
        final java.lang.Object this$birthday = this.getBirthday();
        final java.lang.Object other$birthday = other.getBirthday();
        if (this$birthday == null ? other$birthday != null : !this$birthday.equals(other$birthday)) return false;
        final java.lang.Object this$createdAt = this.getCreatedAt();
        final java.lang.Object other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) return false;
        final java.lang.Object this$modifiedAt = this.getModifiedAt();
        final java.lang.Object other$modifiedAt = other.getModifiedAt();
        return this$modifiedAt == null ? other$modifiedAt == null : this$modifiedAt.equals(other$modifiedAt);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof NinbotUser;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final java.lang.Object $serverId = this.getServerId();
        result = result * PRIME + ($serverId == null ? 43 : $serverId.hashCode());
        final java.lang.Object $birthday = this.getBirthday();
        result = result * PRIME + ($birthday == null ? 43 : $birthday.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        final java.lang.Object $modifiedAt = this.getModifiedAt();
        result = result * PRIME + ($modifiedAt == null ? 43 : $modifiedAt.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "NinbotUser(id=" + this.getId() + ", userId=" + this.getUserId() + ", serverId=" + this.getServerId()
                + ", birthday=" + this.getBirthday() + ", createdAt=" + this.getCreatedAt() + ", modifiedAt="
                + this.getModifiedAt() + ")";
    }
}
