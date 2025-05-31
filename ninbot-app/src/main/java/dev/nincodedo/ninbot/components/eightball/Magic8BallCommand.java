package dev.nincodedo.ninbot.components.eightball;

import dev.nincodedo.nincord.command.slash.SlashCommand;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.SlashCommandEventMessageExecutor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Magic8BallCommand implements SlashCommand {

    private final Magic8BallMessageBuilder magic8BallMessageBuilder;

    @Override
    public MessageExecutor execute(@NotNull SlashCommandInteractionEvent event,
            @NotNull SlashCommandEventMessageExecutor messageExecutor) {
        var question = event.getOption(Magic8BallCommandName.Option.QUESTION.get(), "", OptionMapping::getAsString);
        var messageEmbed = magic8BallMessageBuilder.question(question)
                .userName(event.getUser().getEffectiveName())
                .build();
        var response = new MessageCreateBuilder().addEmbeds(messageEmbed)
                .addComponents(ActionRow.of(Button.primary("%s-reshake-%s".formatted(getName(), event.getUser()
                        .getId()), "Shake Again")))
                .build();
        messageExecutor.addMessageResponse(response);
        return messageExecutor;
    }

    @Override
    public String getName() {
        return Magic8BallCommandName.EIGHTBALL.get();
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(new OptionData(OptionType.STRING, Magic8BallCommandName.Option.QUESTION.get(), "Your question "
                + "to the 8 ball."));
    }
}
