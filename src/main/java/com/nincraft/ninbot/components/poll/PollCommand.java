package com.nincraft.ninbot.components.poll;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

@Log4j2
@Component
public class PollCommand extends AbstractCommand {

    public PollCommand() {
        name = "poll";
        length = 4;
        checkExactLength = false;
    }

    @Override
    public CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        if (isCommandLengthCorrect(event.getMessage().getContentStripped())) {
            Poll poll = parsePollMessage(event.getMessage(), event.getAuthor());
            poll.setResourceBundle(resourceBundle);
            if (!poll.getChoices().isEmpty() && poll.getChoices().size() <= 9) {
                event.getChannel().sendMessage(poll.build()).queue(new PollConsumer(poll));
            } else {
                commandResult.addUnsuccessfulReaction();
            }
        }
        return commandResult;
    }

    private Poll parsePollMessage(Message message, User user) {
        Poll poll = new Poll();
        poll.setUser(user);
        val pollMessage = message.getContentStripped().substring("@Ninbot poll ".length());
        poll.setChoices(new ArrayList<>());
        if (pollMessage.contains("\"")) {
            poll.setTitle(pollMessage.substring(0, pollMessage.indexOf("\"")));
            val pollOptions = pollMessage.substring(
                    pollMessage.indexOf("\"") + 1, pollMessage.lastIndexOf("\"")).replace("\"", "");
            poll.setChoices(Arrays.asList(pollOptions.split(", ")));
            val timeString = pollMessage.substring(pollMessage.lastIndexOf("\"") + 1).trim();
            if (StringUtils.isNotBlank(timeString)) {
                poll.setTimeLength(Long.parseLong(timeString));
            } else {
                poll.setTimeLength(5L);
            }
        }
        return poll;
    }

}
