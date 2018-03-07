package com.nincraft.ninbot.components.poll;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.util.MessageUtils;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Log4j2
public class PollCommand extends AbstractCommand {

    public PollCommand() {
        name = "poll";
        description = "Creates a new poll, arguments are (question \"answer 1, answer 2, answer 3...\" pollLengthInMinutes)\n" +
                "Example: @Ninbot poll Who stole the cookies from the cookie jar? \"You, me, then who?\" 20\n" +
                "This would create a poll with three options and it would close after 20 minutes.";
        length = 4;
        checkExactLength = false;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        if (getCommandLength(event.getMessage().getContent()) == 2) {
            MessageUtils.sendMessage(event.getChannel(), description);
            return;
        }
        Poll poll = new Poll(event);
        if (!poll.getChoices().isEmpty()) {
            event.getChannel().sendMessage(poll.build()).queue(new PollConsumer(poll));
        } else {
            MessageUtils.reactUnsuccessfulResponse(event.getMessage());
        }
    }

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
            int index = 0;
            List<MessageReaction> reactions = newMessage.getReactions();
            for (int i = 0; i < reactions.size() && i < poll.getChoices().size(); i++) {
                MessageReaction reaction = reactions.get(i);
                if (reaction.getCount() > highCount) {
                    highCount = reaction.getCount();
                    index = i;
                }
            }

            String pollClosedMessage = " The poll is now closed.";
            if (highCount <= 1) {
                poll.setResult("No one voted in this poll." + pollClosedMessage);
            } else {
                poll.setResult("\"" + poll.getChoices().get(index) + "\" had the most votes with " + (highCount - 1) + " vote." + pollClosedMessage);
            }
            message.editMessage(poll.buildClosed()).queue();
            MessageUtils.sendMessage(message.getChannel(), poll.getResult());
        }
    }
}
