package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class PollAnnouncementSetup {

    private StatManager statManager;
    private ExecutorService executorService;
    private PollService pollService;
    private Map<Long, PollListeners> pollListenersMap;

    PollAnnouncementSetup(PollService pollService, StatManager statManager,
            @Qualifier("statCounterThreadPool") ExecutorService executorService) {
        this.pollService = pollService;
        this.statManager = statManager;
        this.executorService = executorService;
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
        PollUserChoiceListener pollUserChoiceListener = new PollUserChoiceListener(statManager, executorService,
                pollService, poll.getMessageId(), this);
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
            message.addReaction(Emoji.fromFormatted(digitalOneEmoji + "\u20E3")).queue();
            digitalOneEmoji++;
        }
    }

    /**
     * Twice a day remove closed polls from the map, just in case.
     */
    @Scheduled(fixedRate = 12, timeUnit = TimeUnit.HOURS)
    void removeClosedPolls() {
        pollService.findAllClosedPolls().forEach(poll -> pollListenersMap.remove(poll.getId()));
    }

    record PollListeners(Timer timer, PollUserChoiceListener pollUserChoiceListener) {
    }
}
