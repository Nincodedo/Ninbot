package com.nincraft.ninbot.components.twitch;

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
    LocalDateTime started;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String guildId;

    public StreamingMember() {

    }

    StreamingMember(String userId, String guildId) {
        this.userId = userId;
        this.guildId = guildId;
        this.started = LocalDateTime.now();
    }
}
