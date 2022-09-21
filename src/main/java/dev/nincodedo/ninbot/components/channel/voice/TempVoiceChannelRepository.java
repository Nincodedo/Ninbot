package dev.nincodedo.ninbot.components.channel.voice;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TempVoiceChannelRepository extends CrudRepository<TempVoiceChannel, Long> {

    Optional<TempVoiceChannel> findByVoiceChannelId(String voiceChannelId);
}
