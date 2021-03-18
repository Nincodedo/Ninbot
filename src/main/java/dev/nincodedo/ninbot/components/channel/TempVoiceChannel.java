package dev.nincodedo.ninbot.components.channel;

import javax.persistence.*;

@Entity
public class TempVoiceChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    @Column(nullable = false)
    private String voiceChannelId;
    @Column(nullable = false)
    private String userId;

    TempVoiceChannel(String userId, String voiceChannelId) {
        this.userId = userId;
        this.voiceChannelId = voiceChannelId;
    }

    public TempVoiceChannel() {
        //no-op
    }


    public long getId() {
        return this.id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getVoiceChannelId() {
        return this.voiceChannelId;
    }

    public void setVoiceChannelId(final String voiceChannelId) {
        this.voiceChannelId = voiceChannelId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof TempVoiceChannel)) return false;
        final TempVoiceChannel other = (TempVoiceChannel) o;
        if (!other.canEqual(this)) return false;
        if (this.getId() != other.getId()) return false;
        final java.lang.Object this$voiceChannelId = this.getVoiceChannelId();
        final java.lang.Object other$voiceChannelId = other.getVoiceChannelId();
        if (this$voiceChannelId == null ?
                other$voiceChannelId != null : !this$voiceChannelId.equals(other$voiceChannelId)) return false;
        final java.lang.Object this$userId = this.getUserId();
        final java.lang.Object other$userId = other.getUserId();
        return this$userId == null ? other$userId == null : this$userId.equals(other$userId);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof TempVoiceChannel;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $id = this.getId();
        result = result * PRIME + (int) ($id >>> 32 ^ $id);
        final java.lang.Object $voiceChannelId = this.getVoiceChannelId();
        result = result * PRIME + ($voiceChannelId == null ? 43 : $voiceChannelId.hashCode());
        final java.lang.Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "TempVoiceChannel(id=" + this.getId() + ", voiceChannelId=" + this.getVoiceChannelId() + ", userId="
                + this.getUserId() + ")";
    }
}
