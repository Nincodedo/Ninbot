package dev.nincodedo.ninbot.components.stream;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class StreamInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime startTimestamp = LocalDateTime.now();
    private LocalDateTime endTimestamp;
    private String announceMessageId;
    @ManyToOne
    private StreamingMember streamingMember;

    public StreamInstance() {
    }

    public boolean isStreaming() {
        return endTimestamp == null;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTimestamp() {
        return this.startTimestamp;
    }

    public void setStartTimestamp(final LocalDateTime startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public LocalDateTime getEndTimestamp() {
        return this.endTimestamp;
    }

    public void setEndTimestamp(final LocalDateTime endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getAnnounceMessageId() {
        return this.announceMessageId;
    }

    public void setAnnounceMessageId(final String announceMessageId) {
        this.announceMessageId = announceMessageId;
    }

    public StreamingMember getStreamingMember() {
        return this.streamingMember;
    }

    public void setStreamingMember(final StreamingMember streamingMember) {
        this.streamingMember = streamingMember;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof StreamInstance)) return false;
        final StreamInstance other = (StreamInstance) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$startTimestamp = this.getStartTimestamp();
        final java.lang.Object other$startTimestamp = other.getStartTimestamp();
        if (this$startTimestamp == null ?
                other$startTimestamp != null : !this$startTimestamp.equals(other$startTimestamp)) return false;
        final java.lang.Object this$endTimestamp = this.getEndTimestamp();
        final java.lang.Object other$endTimestamp = other.getEndTimestamp();
        if (this$endTimestamp == null ? other$endTimestamp != null : !this$endTimestamp.equals(other$endTimestamp))
            return false;
        final java.lang.Object this$announceMessageId = this.getAnnounceMessageId();
        final java.lang.Object other$announceMessageId = other.getAnnounceMessageId();
        if (this$announceMessageId == null ?
                other$announceMessageId != null : !this$announceMessageId.equals(other$announceMessageId)) return false;
        final java.lang.Object this$streamingMember = this.getStreamingMember();
        final java.lang.Object other$streamingMember = other.getStreamingMember();
        return this$streamingMember == null ?
                other$streamingMember == null : this$streamingMember.equals(other$streamingMember);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof StreamInstance;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $startTimestamp = this.getStartTimestamp();
        result = result * PRIME + ($startTimestamp == null ? 43 : $startTimestamp.hashCode());
        final java.lang.Object $endTimestamp = this.getEndTimestamp();
        result = result * PRIME + ($endTimestamp == null ? 43 : $endTimestamp.hashCode());
        final java.lang.Object $announceMessageId = this.getAnnounceMessageId();
        result = result * PRIME + ($announceMessageId == null ? 43 : $announceMessageId.hashCode());
        final java.lang.Object $streamingMember = this.getStreamingMember();
        result = result * PRIME + ($streamingMember == null ? 43 : $streamingMember.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "StreamInstance(id=" + this.getId() + ", startTimestamp=" + this.getStartTimestamp() + ", endTimestamp="
                + this.getEndTimestamp() + ", announceMessageId=" + this.getAnnounceMessageId() + ")";
    }
}
