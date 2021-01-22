package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.common.Schedulable;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.val;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PollScheduler implements Schedulable {

    private PollRepository pollRepository;
    private ExecutorService executorService;
    private PollSetup pollSetup;

    public PollScheduler(PollRepository pollRepository, PollSetup pollSetup) {
        this.pollRepository = pollRepository;
        this.executorService = Executors.newCachedThreadPool(new NamedThreadFactory("poll-scheduler"));
        this.pollSetup = pollSetup;
    }

    @Override
    public void scheduleAll(ShardManager shardManager) {
        //Only find open polls (ones that have not been announced)
        pollRepository.findAllByPollOpen(true)
                .forEach(poll -> executorService.execute(() -> scheduleOne(poll, shardManager)));
    }

    void addPoll(Poll poll, ShardManager shardManager) {
        pollRepository.save(poll);
        scheduleOne(poll, shardManager);
    }

    private void scheduleOne(Poll poll, ShardManager shardManager) {
        val resourceBundle = LocaleService.getResourceBundleOrDefault(shardManager.getGuildById(poll.getServerId())
                .getLocale());
        poll.setResourceBundle(resourceBundle);
        val channel = shardManager.getTextChannelById(poll.getChannelId());
        if (channel != null) {
            channel.retrieveMessageById(poll.getMessageId())
                    .queue(message -> pollSetup.setupAnnounce(poll, shardManager, message));
        }
    }
}
