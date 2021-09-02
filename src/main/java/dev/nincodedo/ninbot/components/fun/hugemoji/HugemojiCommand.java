package dev.nincodedo.ninbot.components.fun.hugemoji;

import dev.nincodedo.ninbot.common.message.MessageAction;
import dev.nincodedo.ninbot.components.command.AbstractCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

@Slf4j
public class HugemojiCommand extends AbstractCommand {

    public HugemojiCommand() {
        name = "hugemoji";
        length = 3;
    }

    //TODO implement SlashCommand
    @Override
    protected MessageAction executeCommand(PrivateMessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);

        return messageAction;
    }
}
