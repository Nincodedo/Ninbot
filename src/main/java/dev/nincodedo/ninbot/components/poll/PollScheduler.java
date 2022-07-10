package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.common.LocaleService;
import dev.nincodedo.ninbot.common.Schedulable;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PollScheduler implements Schedulable<Poll, PollService> {

    private PollService pollService;
    private PollAnnouncementSetup pollAnnouncementSetup;

    public PollScheduler(PollService pollService, PollAnnouncementSetup pollAnnouncementSetup) {
        this.pollService = pollService;
        this.pollAnnouncementSetup = pollAnnouncementSetup;
    }

    @Override
    public void scheduleOne(Poll poll, ShardManager shardManager) {
        var resourceBundle = LocaleService.getResourceBundleOrDefault(shardManager.getGuildById(poll.getServerId()));
        poll.setResourceBundle(resourceBundle);
        var guildChannel = shardManager.getGuildChannelById(poll.getChannelId());
        if (guildChannel == null) {
            return;
        }
        var channel = switch (guildChannel.getType()) {
            case TEXT -> (TextChannel) guildChannel;
            case GUILD_PUBLIC_THREAD, GUILD_PRIVATE_THREAD -> (ThreadChannel) guildChannel;
            default -> throw new IllegalStateException("Unexpected value: " + guildChannel.getType());
        };
        channel.retrieveMessageById(poll.getMessageId())
                .queue(message -> pollAnnouncementSetup.setupAnnounce(poll, shardManager, message));
    }

    @Override
    public PollService getScheduler() {
        return pollService;
    }
}
