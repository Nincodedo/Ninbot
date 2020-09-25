package dev.nincodedo.ninbot.components.common;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Component
@Log4j2
public class LocaleService {

    private Locale defaultLocale;

    public LocaleService() {
        this.defaultLocale = Locale.ENGLISH;
    }

    public Locale getLocale(MessageReceivedEvent event) {
        if (event.getChannelType().isGuild()) {
            return event.getGuild().getLocale();
        } else {
            return defaultLocale;
        }
    }

    public Locale getLocale(Guild guild) {
        return guild.getLocale();
    }

    public ResourceBundle getResourceBundleOrDefault(Guild guild) {
        return getResourceBundleOrDefault(guild.getLocale());
    }

    public ResourceBundle getResourceBundleOrDefault(Locale serverLocale) {
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
