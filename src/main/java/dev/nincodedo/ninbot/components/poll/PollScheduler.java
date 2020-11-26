package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.common.Schedulable;
import lombok.val;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.function.Consumer;

@Component
public class PollScheduler implements Schedulable {

    private PollRepository pollRepository;
    private LocaleService localeService;

    public PollScheduler(PollRepository pollRepository, LocaleService localeService) {
        this.pollRepository = pollRepository;
        this.localeService = localeService;
    }

    @Override
    public void scheduleAll(ShardManager shardManager) {
        //Only find open polls (ones that have not been announced)
        pollRepository.findAllByPollOpen(true).forEach(poll -> scheduleOne(poll, shardManager));
    }

    void addPoll(Poll poll, ShardManager shardManager) {
        pollRepository.save(poll);
        scheduleOne(poll, shardManager);
    }

    private void scheduleOne(Poll poll, ShardManager shardManager) {
        val resourceBundle = localeService.getResourceBundleOrDefault(shardManager.getGuildById(poll.getServerId())
                .getLocale());
        poll.setResourceBundle(resourceBundle);
        List<String> choices = poll.getChoices();
        val channel = shardManager.getTextChannelById(poll.getChannelId());
        if (channel != null) {
            channel.retrieveMessageById(poll.getMessageId()).queue(setupPoll(poll, choices));
        }
    }

    private Consumer<Message> setupPoll(Poll poll, List<String> choices) {
        return message -> {
            char digitalOneEmoji = '\u0031';
            for (int i = 0; i < choices.size(); i++) {
                message.addReaction(digitalOneEmoji + "\u20E3").queue();
                digitalOneEmoji++;
            }
            val announceTime = message.getTimeCreated().toInstant().plus(poll.getTimeLength(), ChronoUnit.MINUTES);
            //Poll announce time is in the future, or has not been announce yet
            if (announceTime.isAfter(Instant.now()) || poll.isPollOpen()) {
                message.pin().queue();
                PollAnnounce pollAnnounce = new PollAnnounce(poll, message, pollRepository);
                Timer timer = new Timer();
                timer.schedule(pollAnnounce, Date.from(announceTime));
            }
        };
    }
}
