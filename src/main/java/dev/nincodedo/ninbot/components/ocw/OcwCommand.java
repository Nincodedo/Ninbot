package dev.nincodedo.ninbot.components.ocw;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.Constants;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class OcwCommand extends AbstractCommand {
    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        val guildId = event.getGuild().getId();
        if (Constants.OCW_SERVER_ID.equals(guildId)) {
            return executeOcwCommand(event);
        } else {
            return new MessageAction().addUnsuccessfulReaction();
        }
    }

    protected abstract MessageAction executeOcwCommand(MessageReceivedEvent event);
}
