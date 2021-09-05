package dev.nincodedo.ninbot.components.fun.define;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class DefineCommand implements SlashCommand {

    private DefineWordAPI defineWordAPI;

    public DefineCommand(DefineWordAPI defineWordAPI) {
        this.defineWordAPI = defineWordAPI;
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        String word = slashCommandEvent.getOption("word").getAsString();
        Map<String, String> definition = defineWordAPI.defineWord(word);
        if (definition == null) {
            slashCommandEvent.reply(Emojis.CROSS_X).setEphemeral(true).queue();
        } else {
            slashCommandEvent.reply(buildMessage(definition, word)).queue();
        }
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
        return "define";
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(new OptionData(OptionType.STRING, "word", "The word to lookup.", true));
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Collections.emptyList();
    }
}
