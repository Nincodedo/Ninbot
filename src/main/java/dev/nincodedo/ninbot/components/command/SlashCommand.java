package dev.nincodedo.ninbot.components.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public interface SlashCommand {
    String getName();

    default String getDescription() {
        return ResourceBundle.getBundle("lang", Locale.ENGLISH).getString("command." + getName() + ".description.text");
    }

    List<OptionData> getCommandOptions();

    List<SubcommandData> getSubcommandDatas();

    void execute(SlashCommandEvent slashCommandEvent);
}

