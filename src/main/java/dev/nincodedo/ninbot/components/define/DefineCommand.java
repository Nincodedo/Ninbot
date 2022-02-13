package dev.nincodedo.ninbot.components.define;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
        String word = slashCommandEvent.getOption(DefineCommandName.Option.WORD.get()).getAsString();
        Map<String, String> definition = defineWordAPI.defineWord(word);
        if (definition == null) {
            messageExecutor.addEphemeralMessage(Emojis.CROSS_X);
        } else {
            messageExecutor.addMessageResponse(buildMessage(definition, word));
        }
        return messageExecutor;
    }

    private Message buildMessage(Map<String, String> definition, String word) {
        return new MessageBuilder(
                new EmbedBuilder()
                        .setTitle("Definition of " + word)
                        .addField(word, definition.get("definition").split("\n")[0], false)
                        .addField("Find out more", definition.get("permalink"), false)).build();
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
