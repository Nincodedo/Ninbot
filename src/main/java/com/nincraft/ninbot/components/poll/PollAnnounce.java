package com.nincraft.ninbot.components.poll;

import lombok.val;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

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
        val newMessage = message.getChannel().retrieveMessageById(message.getId()).complete();
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
        val resourceBundle = poll.getResourceBundle();

        String pollClosedMessage = resourceBundle.getString("poll.announce.closed");
        if (highCount <= 1) {
            poll.setResult(resourceBundle.getString("poll.announce.noonevoted") + pollClosedMessage);
        } else if (winningChoices.size() == 1) {
            poll.setResult(
                    "\"" + winningChoices.get(0) + "\"" + resourceBundle.getString("poll.announce.hadmostvotes") + (
                            highCount - 1) + resourceBundle.getString("poll.announce.votes")
                            + pollClosedMessage);
        } else if (winningChoices.size() > 1) {
            poll.setResult(
                    String.format(resourceBundle.getString("poll.announce.tie"), listWinners(winningChoices), (highCount
                            - 1))
                            + pollClosedMessage);
        }
        message.editMessage(poll.buildClosed()).queue();
        message.getChannel().sendMessage(poll.getResult()).queue();
        message.unpin().queue();
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
