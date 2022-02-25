package dev.nincodedo.ninbot.components.channel;

import dev.nincodedo.ninbot.common.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class TempVoiceChannel extends BaseEntity {

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
