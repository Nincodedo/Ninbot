package dev.nincodedo.ninbot.components.ocw.gameserver;

import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.ocw.OcwCommand;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GameServerCommand extends OcwCommand {

    public GameServerCommand() {
        name = "servers";
        length = 3;
        checkExactLength = false;
        aliases = Collections.singletonList("server");
    }

    @Override
    protected MessageAction executeOcwCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction();
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "list" -> messageAction.addChannelAction(listAvailableGameServers(event));
            case "start" -> messageAction.addCorrectReaction(startGameServer(event));
            default -> messageAction = displayHelp(event);
        }

        return messageAction;
    }

    private boolean startGameServer(MessageReceivedEvent event) {
        return false;
    }

    private String listAvailableGameServers(MessageReceivedEvent event) {
        return null;
    }
}
