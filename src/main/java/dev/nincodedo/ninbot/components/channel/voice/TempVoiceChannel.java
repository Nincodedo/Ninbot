package dev.nincodedo.ninbot.components.channel.voice;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

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