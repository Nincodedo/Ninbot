package dev.nincodedo.ninbot.components.channel.voice;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.Optional;

public interface TempVoiceChannelRepository extends BaseRepository<TempVoiceChannel> {
    default Optional<TempVoiceChannel> findByVoiceChannelId(String voiceChannelId) {
        return findByVoiceChannelIdAndDeleted(voiceChannelId, false);
    }

    Optional<TempVoiceChannel> findByVoiceChannelIdAndDeleted(String voiceChannelId, Boolean isDeleted);
}
