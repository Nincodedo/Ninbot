package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
class StreamingMember extends BaseEntity {
    private String userId;
    private String guildId;
    private String twitchUsername;
    @Column(columnDefinition = "boolean default false", nullable = false)
    private Boolean announceEnabled = false;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "streamingMember", fetch = FetchType.EAGER)
    private List<StreamInstance> streamInstances = new ArrayList<>();

    public StreamingMember() {

    }

    StreamingMember(String userId, String guildId) {
        this.userId = userId;
        this.guildId = guildId;
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
}
