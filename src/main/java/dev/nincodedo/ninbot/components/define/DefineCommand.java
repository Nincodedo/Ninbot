package dev.nincodedo.ninbot.components.define;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefineCommand implements SlashCommand {

    private DefineWordAPI defineWordAPI;

    public DefineCommand(DefineWordAPI defineWordAPI) {
        this.defineWordAPI = defineWordAPI;
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        String word = slashCommandEvent.getOption(DefineCommandName.Option.WORD.get(), OptionMapping::getAsString);
        Word wordDefinition = defineWordAPI.defineWord(word);
        if (wordDefinition == null) {
            messageExecutor.addEphemeralMessage(Emojis.CROSS_X);
        } else {
            messageExecutor.addMessageResponse(buildMessage(wordDefinition));
        }
        return messageExecutor;
    }

    private Message buildMessage(Word wordDefinition) {
        return new MessageBuilder(
                new EmbedBuilder()
                        .setTitle("Definition of " + wordDefinition.word())
                        .addField(wordDefinition.word(), wordDefinition.definition().split("\n")[0], false))
                .setActionRows(ActionRow.of(Button.link(wordDefinition.link(), "Find Out More")))
                .build();
    }

    @Override
    public String getName() {
        return DefineCommandName.DEFINE.get();
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(new OptionData(OptionType.STRING, DefineCommandName.Option.WORD.get(), "The word to "
                + "lookup.", true));
    }
}
