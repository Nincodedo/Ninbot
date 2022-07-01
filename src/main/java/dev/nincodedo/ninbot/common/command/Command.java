package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.release.ReleaseStage;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public interface Command<T, F> extends ReleaseStage {
    Locale defaultLocale = Locale.ENGLISH;

    /**
     * Returns the name of the Command.
     *
     * @return String name
     */
    String getName();

    MessageExecutor<T> execute(@NotNull F event);

    default boolean isAbleToRegisterOnServer() {
        return true;
    }
}
