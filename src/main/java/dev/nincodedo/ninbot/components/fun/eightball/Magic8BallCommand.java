package dev.nincodedo.ninbot.components.fun.eightball;

import dev.nincodedo.ninbot.common.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class Magic8BallCommand implements SlashCommand {

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        Magic8BallMessageBuilder magic8BallMessageBuilder = new Magic8BallMessageBuilder();
        var questionOption = slashCommandEvent.getOption("question");
        var question = questionOption != null ? questionOption.getAsString() : "";
        var message = magic8BallMessageBuilder
                .question(question)
                .memberName(slashCommandEvent.getMember().getEffectiveName())
                .build();
        slashCommandEvent.reply(message).queue();
    }

    @Override
    public String getName() {
        return "8ball";
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(new OptionData(OptionType.STRING, "question", "Your question to the 8 ball."));
    }
}
