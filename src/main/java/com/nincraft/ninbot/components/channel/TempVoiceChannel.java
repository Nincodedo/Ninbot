package com.nincraft.ninbot.components.channel;

import lombok.Data;

import javax.persistence.*;

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
