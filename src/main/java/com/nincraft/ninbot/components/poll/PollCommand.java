package com.nincraft.ninbot.components.poll;

import com.nincraft.ninbot.components.command.AbstractCommand;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;


@Log4j2
@Component
public class PollCommand extends AbstractCommand {

    public PollCommand() {
        name = "poll";
        description = "Creates a new poll. Use poll help for more information";
        length = 4;
        checkExactLength = false;
        helpText =
                "Creates a new poll, arguments are (question \"answer 1, answer 2, answer 3...\" pollLengthInMinutes)\n"
                        +
                "Example: @Ninbot poll Who stole the cookies from the cookie jar? \"You, me, then who?\" 20\n" +
                "This would create a poll with three options and it would close after 20 minutes.";
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        Poll poll = parsePollMessage(event.getMessage());
        if (!poll.getChoices().isEmpty() && poll.getChoices().size() <= 9) {
            event.getChannel().sendMessage(poll.build()).queue(new PollConsumer(poll, messageUtils));
        } else {
            messageUtils.reactUnsuccessfulResponse(event.getMessage());
        }
    }

    private Poll parsePollMessage(Message message) {
        Poll poll = new Poll();
        val pollMessage = message.getContentStripped().substring("@Ninbot poll ".length());
        poll.setChoices(new ArrayList<>());
        if (pollMessage.contains("\"")) {
            poll.setTitle(pollMessage.substring(0, pollMessage.indexOf("\"")));
            val pollOptions = pollMessage.substring(
                    pollMessage.indexOf("\"") + 1, pollMessage.lastIndexOf("\"")).replace("\"", "");
            poll.setChoices(Arrays.asList(pollOptions.split(", ")));
            val timeString = pollMessage.substring(pollMessage.lastIndexOf("\"") + 1).trim();
            if (StringUtils.isNotBlank(timeString)) {
                poll.setTimeLength(Long.valueOf(timeString));
            } else {
                poll.setTimeLength(5L);
            }
        }
        return poll;
    }

}
