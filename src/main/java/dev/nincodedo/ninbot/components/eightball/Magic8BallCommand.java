package dev.nincodedo.ninbot.components.eightball;

import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Magic8BallCommand implements SlashCommand {

    private Magic8BallMessageBuilder magic8BallMessageBuilder;

    public Magic8BallCommand(Magic8BallMessageBuilder magic8BallMessageBuilder) {
        this.magic8BallMessageBuilder = magic8BallMessageBuilder;
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        var questionOption = slashCommandEvent.getOption(Magic8BallCommandName.Option.QUESTION.get());
        var question = questionOption != null ? questionOption.getAsString() : "";
        var messageEmbed = magic8BallMessageBuilder
                .question(question)
                .memberName(slashCommandEvent.getMember().getEffectiveName())
                .build();
        messageExecutor.addMessageEmbed(messageEmbed);
        return messageExecutor;
    }

    @Override
    public String getName() {
        return Magic8BallCommandName.EIGHTBALL.get();
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(new OptionData(OptionType.STRING, Magic8BallCommandName.Option.QUESTION.get(), "Your "
                + "question to the 8 ball."));
    }
}
