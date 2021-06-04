package dev.nincodedo.ninbot.components.command;

import net.dv8tion.jda.api.entities.Command;

public record CommandOption(Command.OptionType type, String name, String description, boolean required) {
}
