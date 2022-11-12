package dev.nincodedo.ninbot.common.command;

import org.jetbrains.annotations.NotNull;

public interface Subcommand<T extends Enum<T>> {
    Class<T> enumSubcommandClass();

    /**
     * Returns the Enum subcommand of the input String.
     *
     * @param subcommand String subcommand
     * @return Enum subcommand that matches the string
     */
    @NotNull
    default T getSubcommand(@NotNull String subcommand) {
        return Enum.valueOf(enumSubcommandClass(), subcommand.toUpperCase());
    }
}
