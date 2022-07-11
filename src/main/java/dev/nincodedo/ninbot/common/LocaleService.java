package dev.nincodedo.ninbot.common;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@UtilityClass
@Slf4j
public class LocaleService {

    public static final String BUNDLE_BASE_NAME = "lang";
    private static Locale defaultLocale = Locale.ENGLISH;

    public static Locale getLocale(Guild guild) {
        return guild.getLocale();
    }

    public static ResourceBundle getResourceBundleOrDefault(Guild guild) {
        if (guild == null) {
            log.trace("Guild was null, using default locale");
            return ResourceBundle.getBundle(BUNDLE_BASE_NAME, defaultLocale);
        } else {
            return getResourceBundleOrDefault(guild.getLocale());
        }
    }

    private ResourceBundle getResourceBundleOrDefault(Locale guildLocale) {
        try {
            return ResourceBundle.getBundle(BUNDLE_BASE_NAME, guildLocale);
        } catch (MissingResourceException e) {
            log.trace("Could not find resource bundle for locale {}, using default locale", guildLocale);
            return ResourceBundle.getBundle(BUNDLE_BASE_NAME, defaultLocale);
        }
    }
}
