package dev.nincodedo.ninbot.common;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@UtilityClass
@Slf4j
public class LocaleService {

    private static Locale defaultLocale = Locale.ENGLISH;

    public static Locale getLocale(MessageReceivedEvent event) {
        if (event.getChannelType().isGuild()) {
            return event.getGuild().getLocale();
        } else {
            return defaultLocale;
        }
    }

    public static Locale getLocale(Guild guild) {
        return guild.getLocale();
    }

    public static ResourceBundle getResourceBundleOrDefault(Guild guild) {
        return getResourceBundleOrDefault(guild.getLocale());
    }

    public static ResourceBundle getResourceBundleOrDefault(Locale serverLocale) {
        ResourceBundle resourceBundle;
        try {
            resourceBundle = ResourceBundle.getBundle("lang", serverLocale);
        } catch (MissingResourceException e) {
            log.trace("Could not find resource bundle for locale {}, using default", serverLocale);
            resourceBundle = ResourceBundle.getBundle("lang", defaultLocale);
        }
        return resourceBundle;
    }
}
