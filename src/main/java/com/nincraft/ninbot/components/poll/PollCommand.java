package com.nincraft.ninbot.components.poll;

import com.nincraft.ninbot.components.command.AbstractCommand;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;


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
        Poll poll = new Poll(event.getMessage());
        if (!poll.getChoices().isEmpty() && poll.getChoices().size() <= 9) {
            event.getChannel().sendMessage(poll.build()).queue(new PollConsumer(poll, messageUtils));
        } else {
            messageUtils.reactUnsuccessfulResponse(event.getMessage());
        }
    }

}
