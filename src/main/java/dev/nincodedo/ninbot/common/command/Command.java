package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.release.ReleaseStage;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

public interface Command<T, F> extends ReleaseStage {
    Locale defaultLocale = Locale.ENGLISH;

    /**
     * Returns the name of the Command.
     *
     * @return String name
     */
    String getName();

    CommandType getType();

    default boolean isCommandEnabledByDefault() {
        return true;
    }

    MessageExecutor execute(@NotNull F event);

    default boolean isAbleToRegisterOnGuild() {
        return true;
    }

    default ResourceBundle resourceBundle() {
        return resourceBundle(defaultLocale);
    }

    default ResourceBundle resourceBundle(Locale locale) {
        return ResourceBundle.getBundle("lang", locale);
    }

    default ResourceBundle resourceBundle(DiscordLocale discordLocale) {
        return ResourceBundle.getBundle("lang", Locale.forLanguageTag(discordLocale.getLocale()));
    }

    default String resource(String resourceBundleKey) {
        return resourceBundle().getString(resourceBundleKey);
    }
}
