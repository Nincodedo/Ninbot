package dev.nincodedo.ninbot.components.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public interface SlashCommand {
    String getName();

    String getDescription();

    List<OptionData> getCommandOptions();

    List<SubcommandData> getSubcommandDatas();

    void execute(SlashCommandEvent slashCommandEvent);
}

