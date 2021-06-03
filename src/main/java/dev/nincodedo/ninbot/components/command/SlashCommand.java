package dev.nincodedo.ninbot.components.command;

import lombok.Data;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface SlashCommand {
    String getName();
    void execute(SlashCommandEvent slashCommandEvent);
}
