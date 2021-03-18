package dev.nincodedo.ninbot.components.stream;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Entity
class StreamingMember {
    @CreatedDate
    private LocalDateTime started;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String guildId;
    private String twitchUsername;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "streamingMember", fetch = FetchType.EAGER)
    private List<StreamInstance> streamInstances = new ArrayList<>();

    public StreamingMember() {
    }

    StreamingMember(String userId, String guildId) {
        this.userId = userId;
        this.guildId = guildId;
        this.started = LocalDateTime.now();
    }

    public void startNewStream() {
        StreamInstance streamInstance = new StreamInstance();
        streamInstances.add(streamInstance);
        streamInstance.setStreamingMember(this);
    }

    public Optional<StreamInstance> currentStream() {
        return streamInstances.stream()
                .sorted(Comparator.comparing(StreamInstance::getStartTimestamp))
                .filter(streamInstance -> streamInstance.getEndTimestamp() == null)
                .findFirst();
    }

    public void updateCurrentStream(StreamInstance streamInstance) {
        currentStream().ifPresent(currentStreamInstance -> {
            streamInstances.remove(currentStreamInstance);
            streamInstances.add(streamInstance);
        });
    }


    public LocalDateTime getStarted() {
        return this.started;
    }

    public void setStarted(final LocalDateTime started) {
        this.started = started;
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

    public String getGuildId() {
        return this.guildId;
    }

    public void setGuildId(final String guildId) {
        this.guildId = guildId;
    }

    public String getTwitchUsername() {
        return this.twitchUsername;
    }

    public void setTwitchUsername(final String twitchUsername) {
        this.twitchUsername = twitchUsername;
    }

    public List<StreamInstance> getStreamInstances() {
        return this.streamInstances;
    }

    public void setStreamInstances(final List<StreamInstance> streamInstances) {
        this.streamInstances = streamInstances;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof StreamingMember)) return false;
        final StreamingMember other = (StreamingMember) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$started = this.getStarted();
        final java.lang.Object other$started = other.getStarted();
        if (this$started == null ? other$started != null : !this$started.equals(other$started)) return false;
        final java.lang.Object this$userId = this.getUserId();
        final java.lang.Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final java.lang.Object this$guildId = this.getGuildId();
        final java.lang.Object other$guildId = other.getGuildId();
        if (this$guildId == null ? other$guildId != null : !this$guildId.equals(other$guildId)) return false;
        final java.lang.Object this$twitchUsername = this.getTwitchUsername();
        final java.lang.Object other$twitchUsername = other.getTwitchUsername();
        if (this$twitchUsername == null ?
                other$twitchUsername != null : !this$twitchUsername.equals(other$twitchUsername)) return false;
        final java.lang.Object this$streamInstances = this.getStreamInstances();
        final java.lang.Object other$streamInstances = other.getStreamInstances();
        return this$streamInstances == null ?
                other$streamInstances == null : this$streamInstances.equals(other$streamInstances);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof StreamingMember;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $started = this.getStarted();
        result = result * PRIME + ($started == null ? 43 : $started.hashCode());
        final java.lang.Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final java.lang.Object $guildId = this.getGuildId();
        result = result * PRIME + ($guildId == null ? 43 : $guildId.hashCode());
        final java.lang.Object $twitchUsername = this.getTwitchUsername();
        result = result * PRIME + ($twitchUsername == null ? 43 : $twitchUsername.hashCode());
        final java.lang.Object $streamInstances = this.getStreamInstances();
        result = result * PRIME + ($streamInstances == null ? 43 : $streamInstances.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "StreamingMember(started=" + this.getStarted() + ", id=" + this.getId() + ", userId=" + this.getUserId()
                + ", guildId=" + this.getGuildId() + ", twitchUsername=" + this.getTwitchUsername()
                + ", streamInstances=" + this.getStreamInstances() + ")";
    }
}
