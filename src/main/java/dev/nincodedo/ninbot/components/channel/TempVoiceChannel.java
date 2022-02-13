package dev.nincodedo.ninbot.components.channel;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
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
}
