package dev.nincodedo.ninbot.components.channel.voice;

import dev.nincodedo.nincord.persistence.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TempVoiceChannel extends BaseEntity {

    @Column(nullable = false)
    private String voiceChannelId;
    @Column(nullable = false)
    private String userId;
}
