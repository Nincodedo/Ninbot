package dev.nincodedo.ninbot.components.channel.voice;

import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledCleanup {
    private final TempVoiceChannelRepository tempVoiceChannelRepository;
    private final ShardManager shardManager;

    public ScheduledCleanup(TempVoiceChannelRepository tempVoiceChannelRepository, ShardManager shardManager) {
        this.tempVoiceChannelRepository = tempVoiceChannelRepository;
        this.shardManager = shardManager;
    }

    @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS, initialDelay = 1)
    public void deleteEmptyTempChannels() {
        tempVoiceChannelRepository.findAll()
                .stream()
                .map(tempVoiceChannel -> shardManager.getVoiceChannelById(tempVoiceChannel.getVoiceChannelId()))
                .filter(Objects::nonNull)
                .filter(voiceChannel -> voiceChannel.getMembers().isEmpty())
                .forEach(voiceChannel -> voiceChannel.delete().queue());
    }
}
