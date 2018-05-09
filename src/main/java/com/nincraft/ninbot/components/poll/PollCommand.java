package com.nincraft.ninbot.components.poll;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.util.MessageUtils;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
        if (getCommandLength(event.getMessage().getContentStripped()) == 2) {
            MessageUtils.sendMessage(event.getChannel(), description);
            return;
        }
        Poll poll = new Poll(event.getAuthor(), event.getMessage());
        if (!poll.getChoices().isEmpty()) {
            event.getChannel().sendMessage(poll.build()).queue(new PollConsumer(poll));
        } else {
            MessageUtils.reactUnsuccessfulResponse(event.getMessage());
        }
    }

}
