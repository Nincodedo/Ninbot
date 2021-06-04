package dev.nincodedo.ninbot.components.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.List;

public interface SlashCommand {
    String getName();

    String getDescription();

    List<CommandOption> getCommandOptions();

    void execute(SlashCommandEvent slashCommandEvent);
}

