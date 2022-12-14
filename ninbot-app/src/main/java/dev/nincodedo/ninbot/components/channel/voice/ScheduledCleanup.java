package dev.nincodedo.ninbot.components.channel.voice;

import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
        tempVoiceChannelRepository.findAll().forEach(tempVoiceChannel -> {
            var voiceChannel = shardManager.getVoiceChannelById(tempVoiceChannel.getVoiceChannelId());
            if (voiceChannel == null) {
                tempVoiceChannelRepository.delete(tempVoiceChannel);
            } else if (voiceChannel.getMembers().isEmpty()) {
                voiceChannel.delete().queue(success -> tempVoiceChannelRepository.delete(tempVoiceChannel));
            }
        });
    }
}
