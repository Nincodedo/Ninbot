package com.nincraft.ninbot.components.poll;

import com.nincraft.ninbot.util.MessageUtils;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Log4j2
class PollConsumer implements Consumer<Message> {

    private static final long MILLISECONDS_CONVERT = 60000;
    private Poll poll;

    PollConsumer(Poll poll) {
        this.poll = poll;
    }

    @Override
    public void accept(Message message) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                char digitalOneEmoji = '\u0031';
                List<String> choices = poll.getChoices();
                for (int i = 0; i < choices.size(); i++) {
                    message.addReaction(Character.toString(digitalOneEmoji) + "\u20E3").queue();
                    digitalOneEmoji++;
                }
                Thread.sleep(poll.getTimeLength() * MILLISECONDS_CONVERT);
                announcePollResults(poll, message);
            } catch (InterruptedException e) {
                log.error(e);
                Thread.currentThread().interrupt();
            }
        });
    }

    private void announcePollResults(Poll poll, Message message) {
        val newMessage = message.getChannel().getMessageById(message.getId()).complete();
        int highCount = 0;
        List<String> winningChoices = new ArrayList<>();
        List<MessageReaction> reactions = newMessage.getReactions();
        for (int i = 0; i < reactions.size() && i < poll.getChoices().size(); i++) {
            MessageReaction reaction = reactions.get(i);
            if (reaction.getCount() == highCount) {
                winningChoices.add(poll.getChoices().get(i));
            }
            if (reaction.getCount() > highCount) {
                highCount = reaction.getCount();
                winningChoices.clear();
                winningChoices.add(poll.getChoices().get(i));
            }
        }

        String pollClosedMessage = " The poll is now closed.";
        if (highCount <= 1) {
            poll.setResult("No one voted in this poll." + pollClosedMessage);
        } else if (winningChoices.size() == 1) {
            poll.setResult("\"" + winningChoices.get(0) + "\" had the most votes with " + (highCount - 1) + " vote(s)." + pollClosedMessage);
        } else if (winningChoices.size() > 1) {
            poll.setResult("It's a tie! " + listWinners(winningChoices) + " won with " + (highCount - 1) + " vote(s) each." + pollClosedMessage);
        }
        message.editMessage(poll.buildClosed()).queue();
        MessageUtils.sendMessage(message.getChannel(), poll.getResult());
    }

    private String listWinners(List<String> winningChoices) {
        StringBuilder winners = new StringBuilder();
        for (int i = 0; i < winningChoices.size(); i++) {
            if (i == winningChoices.size() - 1) {
                winners.append("and ");
            }
            winners.append("\"").append(winningChoices.get(i)).append("\"");
            if (i != winningChoices.size() - 1) {
                winners.append(", ");
            }
        }
        return winners.toString();
    }
}
