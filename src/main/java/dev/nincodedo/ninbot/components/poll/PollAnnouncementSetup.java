package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

@Component
public class PollAnnouncementSetup {

    private StatManager statManager;
    private PollService pollService;
    private Map<Long, PollListeners> pollListenersMap;

    PollAnnouncementSetup(PollService pollService, StatManager statManager) {
        this.pollService = pollService;
        this.statManager = statManager;
        pollListenersMap = new HashMap<>();
    }

    void setupAnnounce(Poll poll, ShardManager shardManager, Message message) {
        var choices = poll.getChoices();
        addPollChoiceEmotes(message, choices);
        var announceTime = poll.getEndDateTime();
        //Poll announce time is in the future, or has not been announced yet
        if (!announceTime.isAfter(LocalDateTime.now()) && !poll.isPollOpen()) {
            return;
        }
        message.pin().queue();
        PollResultsAnnouncer pollResultsAnnouncer = new PollResultsAnnouncer(poll, message, pollService);
        Timer timer;
        PollUserChoiceListener pollUserChoiceListener = new PollUserChoiceListener(statManager,
                pollService, poll
                .getMessageId(), this);
        if (pollListenersMap.containsKey(poll.getId())) {
            PollListeners pollListeners = pollListenersMap.remove(poll.getId());
            pollListeners.timer().cancel();
            shardManager.removeEventListener(pollListeners.pollUserChoiceListener());
        }
        timer = new Timer();
        pollListenersMap.put(poll.getId(), new PollListeners(timer, pollUserChoiceListener));
        if (poll.isUserChoicesAllowed()) {
            shardManager.addEventListener(pollUserChoiceListener);
            pollResultsAnnouncer.setPollUserChoiceListener(pollUserChoiceListener);
        }
        timer.schedule(pollResultsAnnouncer, Date.from(announceTime.atZone(ZoneId.systemDefault()).toInstant()));
    }

    private void addPollChoiceEmotes(Message message, List<String> choices) {
        char digitalOneEmoji = '\u0031';
        for (int i = 0; i < choices.size(); i++) {
            message.addReaction(digitalOneEmoji + "\u20E3").queue();
            digitalOneEmoji++;
        }
    }

    /**
     * Twice a day remove closed polls from the map, just in case.
     */
    @Scheduled(fixedRate = 43200000L)
    void removeClosedPolls() {
        pollService.findAllClosedPolls().forEach(poll -> pollListenersMap.remove(poll.getId()));
    }

    record PollListeners(Timer timer, PollUserChoiceListener pollUserChoiceListener) {
    }
}