package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.val;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class PollSetup {

    private StatManager statManager;
    private PollRepository pollRepository;
    private Map<Long, PollListeners> pollListenersMap;

    PollSetup(PollRepository pollRepository, StatManager statManager) {
        this.pollRepository = pollRepository;
        this.statManager = statManager;
        pollListenersMap = new HashMap<>();
    }

    void setupAnnounce(Poll poll, ShardManager shardManager, Message message) {
        val choices = poll.getChoices();
        addPollChoiceEmotes(message, choices);
        val announceTime = message.getTimeCreated()
                .toInstant()
                .plus(poll.getTimeLength(), ChronoUnit.MINUTES);
        //Poll announce time is in the future, or has not been announced yet
        if (announceTime.isAfter(Instant.now()) || poll.isPollOpen()) {
            message.pin().queue();
            PollResultsAnnouncer pollResultsAnnouncer = new PollResultsAnnouncer(poll, message, pollRepository);
            Timer timer;
            PollUserChoiceListener pollUserChoiceListener = new PollUserChoiceListener(statManager,
                    pollRepository, poll
                    .getMessageId(), this);
            if (pollListenersMap.containsKey(poll.getId())) {
                PollListeners pollListeners = pollListenersMap.remove(poll.getId());
                timer = pollListeners.timer();
                timer.cancel();
                shardManager.removeEventListener(pollListeners.pollUserChoiceListener());
            }
            timer = new Timer();
            pollListenersMap.put(poll.getId(), new PollListeners(timer, pollUserChoiceListener));
            timer.schedule(pollResultsAnnouncer, Date.from(announceTime));
            shardManager.addEventListener(pollUserChoiceListener);
        }
    }

    private void addPollChoiceEmotes(Message message, List<String> choices) {
        char digitalOneEmoji = '\u0031';
        for (int i = 0; i < choices.size(); i++) {
            message.addReaction(digitalOneEmoji + "\u20E3").queue();
            digitalOneEmoji++;
        }
    }

    /**
     * Twice a day remove closed polls from the map, just in case
     */
    @Scheduled(fixedRate = 43200000L)
    void removeClosedPolls() {
        for (Map.Entry<Long, PollListeners> entry : pollListenersMap.entrySet()) {
            Long pollId = entry.getKey();
            pollRepository.findById(pollId).ifPresent(poll -> {
                if (!poll.isPollOpen()) {
                    pollListenersMap.remove(pollId);
                }
            });
        }
    }
}

record PollListeners(Timer timer, PollUserChoiceListener pollUserChoiceListener) {
}