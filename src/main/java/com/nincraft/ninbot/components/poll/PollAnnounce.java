package com.nincraft.ninbot.components.poll;

import com.nincraft.ninbot.components.common.MessageUtils;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

class PollAnnounce extends TimerTask {

    private Poll poll;
    private Message message;

    PollAnnounce(Poll poll, Message message) {
        this.poll = poll;
        this.message = message;
    }

    @Override
    public void run() {
        announcePollResults();
    }

    private void announcePollResults() {
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
            poll.setResult("\"" + winningChoices.get(0) + "\" had the most votes with " + (highCount - 1) + " vote(s)."
                    + pollClosedMessage);
        } else if (winningChoices.size() > 1) {
            poll.setResult(
                    "It's a tie! " + listWinners(winningChoices) + " won with " + (highCount - 1) + " vote(s) each."
                            + pollClosedMessage);
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
