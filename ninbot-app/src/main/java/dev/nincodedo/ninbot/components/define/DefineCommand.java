package dev.nincodedo.ninbot.components.define;

import dev.nincodedo.nincord.Emojis;
import dev.nincodedo.nincord.command.slash.SlashCommand;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefineCommand implements SlashCommand {

    private FeignUrbanDictionary defineWordAPI;

    public DefineCommand(FeignUrbanDictionary defineWordAPI) {
        this.defineWordAPI = defineWordAPI;
    }

    @Override
    public MessageExecutor execute(
            @NotNull SlashCommandInteractionEvent event,
            @NotNull SlashCommandEventMessageExecutor messageExecutor) {
        String word = event.getOption(DefineCommandName.Option.WORD.get(), OptionMapping::getAsString);
        List<Word> wordList = defineWordAPI.defineWord(word).list();
        if (wordList.isEmpty() || wordList.get(0) == null) {
            messageExecutor.addEphemeralMessage(Emojis.CROSS_X);
        } else {
            messageExecutor.addMessageResponse(buildMessage(wordList.get(0)));
        }
        return messageExecutor;
    }

    private MessageCreateData buildMessage(Word wordDefinition) {
        return new MessageCreateBuilder()
                .addEmbeds(new EmbedBuilder()
                        .setTitle("Definition of " + wordDefinition.word())
                        .addField(wordDefinition.word(), wordDefinition.definition().split("\n")[0], false).build())
                .addComponents(ActionRow.of(Button.link(wordDefinition.permalink(), "Find Out More")))
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
