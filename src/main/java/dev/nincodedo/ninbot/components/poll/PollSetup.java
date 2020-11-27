package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.val;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Timer;

@Component
public class PollSetup {

    private StatManager statManager;
    private PollRepository pollRepository;

    PollSetup(PollRepository pollRepository, StatManager statManager) {
        this.pollRepository = pollRepository;
        this.statManager = statManager;
    }

    void setupAnnounce(Poll poll, ShardManager shardManager, Message message) {
        val choices = poll.getChoices();
        char digitalOneEmoji = '\u0031';
        for (int i = 0; i < choices.size(); i++) {
            message.addReaction(digitalOneEmoji + "\u20E3").queue();
            digitalOneEmoji++;
        }
        val announceTime = message.getTimeCreated()
                .toInstant()
                .plus(poll.getTimeLength(), ChronoUnit.MINUTES);
        //Poll announce time is in the future, or has not been announce yet
        if (announceTime.isAfter(Instant.now()) || poll.isPollOpen()) {
            message.pin().queue();
            PollResultsAnnouncer pollResultsAnnouncer = new PollResultsAnnouncer(poll, message, pollRepository);
            Timer timer = new Timer();
            timer.schedule(pollResultsAnnouncer, Date.from(announceTime));
            PollUserChoiceListener pollUserChoiceListener = new PollUserChoiceListener(statManager,
                    pollRepository, poll
                    .getMessageId(), this);
            shardManager.addEventListener(pollUserChoiceListener);
        }
    }
}
