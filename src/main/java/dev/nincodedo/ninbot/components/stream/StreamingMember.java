package dev.nincodedo.ninbot.components.stream;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
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
    private boolean currentlyStreaming = false;

    public StreamingMember() {

    }

    StreamingMember(String userId, String guildId) {
        this.userId = userId;
        this.guildId = guildId;
        this.started = LocalDateTime.now();
    }
}
