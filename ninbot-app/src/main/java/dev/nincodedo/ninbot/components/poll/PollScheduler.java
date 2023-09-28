package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.nincord.LocaleService;
import dev.nincodedo.nincord.Schedulable;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class PollScheduler extends Schedulable<Poll, PollService> {

    private PollService pollService;
    private PollAnnouncementSetup pollAnnouncementSetup;

    public PollScheduler(PollService pollService, PollAnnouncementSetup pollAnnouncementSetup, @Qualifier(
            "schedulerThreadPool") ExecutorService executorService) {
        super(executorService);
        this.pollService = pollService;
        this.pollAnnouncementSetup = pollAnnouncementSetup;
    }

    @Override
    protected String getSchedulerName() {
        return "poll";
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
            case NEWS -> (NewsChannel) guildChannel;
            case GUILD_PUBLIC_THREAD, GUILD_PRIVATE_THREAD, GUILD_NEWS_THREAD -> (ThreadChannel) guildChannel;
            default -> throw new IllegalStateException("Unexpected value: " + guildChannel.getType());
        };
        channel.retrieveMessageById(poll.getMessageId())
                .queue(message -> pollAnnouncementSetup.setupAnnounce(poll, shardManager, message),
                        new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, error -> {
                            log.warn("Could not find message {} for poll {}. Closing the poll.", poll.getMessageId(),
                                    poll.getId());
                            poll.setPollOpen(false);
                            pollService.save(poll);
                        }));
    }

    @Override
    public PollService getSchedulerService() {
        return pollService;
    }
}
