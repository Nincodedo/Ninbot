package dev.nincodedo.ninbot.components.poll;

import lombok.Setter;
import lombok.val;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

class PollResultsAnnouncer extends TimerTask {

    private Poll poll;
    private Message pollMessage;
    private PollRepository pollRepository;
    @Setter
    private PollUserChoiceListener pollUserChoiceListener;

    PollResultsAnnouncer(Poll poll, Message pollMessage, PollRepository pollRepository) {
        this.poll = poll;
        this.pollMessage = pollMessage;
        this.pollRepository = pollRepository;
    }

    @Override
    public void run() {
        pollMessage.getChannel().sendMessage(announcePollResults()).queue(message -> pollRepository.save(poll));
        if (pollUserChoiceListener != null) {
            pollMessage.getJDA().getShardManager().removeEventListener(pollUserChoiceListener);
        }
        this.cancel();
    }

    private String announcePollResults() {
        val newMessage = pollMessage.getChannel().retrieveMessageById(pollMessage.getId()).complete();
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
        pollMessage.editMessage(poll.buildClosed()).queue();
        pollMessage.unpin().queue();
        return poll.getResult();
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
