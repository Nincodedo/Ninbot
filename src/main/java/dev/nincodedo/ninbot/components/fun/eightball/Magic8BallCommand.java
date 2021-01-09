package dev.nincodedo.ninbot.components.fun.eightball;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Magic8BallCommand extends AbstractCommand {

    private Magic8BallMessageBuilder magic8BallMessageBuilder;

    public Magic8BallCommand(Magic8BallMessageBuilder magic8BallMessageBuilder) {
        name = "8ball";
        aliases = Arrays.asList("magic8ball", "8");
        this.magic8BallMessageBuilder = magic8BallMessageBuilder;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        EmbedBuilder embedBuilder = magic8BallMessageBuilder.getMagic8BallEmbed(event.getMember().getEffectiveName());
        messageAction.addChannelAction(embedBuilder);
        return messageAction;
    }
}
