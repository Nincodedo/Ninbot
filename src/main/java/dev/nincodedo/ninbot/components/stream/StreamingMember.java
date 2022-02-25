package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Data
@Entity
class StreamingMember extends BaseEntity {
    @CreatedDate
    private LocalDateTime started;
    private String userId;
    private String guildId;
    private String twitchUsername;
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "streamingMember",
            fetch = FetchType.EAGER
    )
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
}
