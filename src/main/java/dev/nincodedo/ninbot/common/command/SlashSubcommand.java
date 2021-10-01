package dev.nincodedo.ninbot.common.command;

public interface SlashSubcommand<T extends Enum<T>> extends SlashCommand{
    Class<T> enumSubcommandClass();

    default T getSubcommand(String subcommand) {
        if (subcommand == null) {
            return null;
        }
        return Enum.valueOf(enumSubcommandClass(), subcommand.toUpperCase());
    }
}
