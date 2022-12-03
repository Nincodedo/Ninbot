package dev.nincodedo.ninbot.components.channel.voice;

import dev.nincodedo.nincord.persistence.BaseRepository;

import java.util.Optional;

public interface TempVoiceChannelRepository extends BaseRepository<TempVoiceChannel> {

    Optional<TempVoiceChannel> findByVoiceChannelId(String voiceChannelId);
}
