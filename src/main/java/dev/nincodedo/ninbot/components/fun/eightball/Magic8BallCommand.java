package dev.nincodedo.ninbot.components.fun.eightball;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.command.SlashCommand;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import lombok.val;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class Magic8BallCommand extends AbstractCommand implements SlashCommand {

    private Magic8BallMessageBuilder magic8BallMessageBuilder;

    public Magic8BallCommand(Magic8BallMessageBuilder magic8BallMessageBuilder) {
        name = "8ball";
        aliases = Arrays.asList("magic8ball", "8");
        this.magic8BallMessageBuilder = magic8BallMessageBuilder;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val originalMessage = event.getMessage().getContentStripped();
        String question = "";
        if (originalMessage.length() > "@ninbot 8ball ".length()) {
            question = originalMessage.substring(originalMessage.toLowerCase().indexOf("@ninbot 8ball "));
        }
        val message = magic8BallMessageBuilder.getMagic8BallEmbed(question, event.getMember().getEffectiveName());
        messageAction.addChannelAction(message);
        return messageAction;
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(new OptionData(OptionType.STRING, "question", "Your question to the 8 ball",
                false));
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        val question = slashCommandEvent.getOption("question").getAsString();
        val message = magic8BallMessageBuilder.getMagic8BallEmbed(question, slashCommandEvent.getMember()
                .getEffectiveName());
        slashCommandEvent.reply(message).queue();
    }
}
