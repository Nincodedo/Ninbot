package dev.nincodedo.nincord.command;

import dev.nincodedo.nincord.message.MessageExecutor;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

public interface Command<F> {
    Locale defaultLocale = Locale.ENGLISH;

    /**
     * Returns the name of the Command.
     *
     * @return String name
     */
    String getName();

    CommandType getType();

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

    default DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.ENABLED;
    }

    default Set<IntegrationType> allowedIntegrations() {
        return Set.of(IntegrationType.GUILD_INSTALL);
    }
}
