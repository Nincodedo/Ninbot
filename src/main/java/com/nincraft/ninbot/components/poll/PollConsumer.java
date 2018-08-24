package com.nincraft.ninbot.components.poll;

import com.nincraft.ninbot.components.common.MessageUtils;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Log4j2
class PollConsumer implements Consumer<Message> {

    private Poll poll;
    private MessageUtils messageUtils;

    PollConsumer(Poll poll, MessageUtils messageUtils) {
        this.poll = poll;
        this.messageUtils = messageUtils;
    }

    @Override
    public void accept(Message message) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            char digitalOneEmoji = '\u0031';
            List<String> choices = poll.getChoices();
            for (int i = 0; i < choices.size(); i++) {
                message.addReaction(Character.toString(digitalOneEmoji) + "\u20E3").queue();
                digitalOneEmoji++;
            }
            val announceTime = Instant.now().plus(poll.getTimeLength(), ChronoUnit.MINUTES);
            PollAnnounce pollAnnounce = new PollAnnounce(poll, message, messageUtils);
            Timer timer = new Timer();
            timer.schedule(pollAnnounce, Date.from(announceTime));
        });
    }
}
