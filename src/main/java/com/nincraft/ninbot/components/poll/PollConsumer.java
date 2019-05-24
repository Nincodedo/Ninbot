package com.nincraft.ninbot.components.poll;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.entities.Message;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Log4j2
class PollConsumer implements Consumer<Message> {

    private Poll poll;

    PollConsumer(Poll poll) {
        this.poll = poll;
    }

    @Override
    public void accept(Message message) {
        Executors.newSingleThreadExecutor().submit(() -> {
            char digitalOneEmoji = '\u0031';
            List<String> choices = poll.getChoices();
            for (int i = 0; i < choices.size(); i++) {
                message.addReaction(digitalOneEmoji + "\u20E3").queue();
                digitalOneEmoji++;
            }
            message.pin().queue();
            val announceTime = Instant.now().plus(poll.getTimeLength(), ChronoUnit.MINUTES);
            PollAnnounce pollAnnounce = new PollAnnounce(poll, message);
            Timer timer = new Timer();
            timer.schedule(pollAnnounce, Date.from(announceTime));
        });
    }
}
