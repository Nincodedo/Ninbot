package dev.nincodedo.ninbot.components.command;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public record CommandOption(OptionType type, String name, String description, boolean required) {
}
