package dev.nincodedo.ninbot.common;

import dev.nincodedo.ninbot.common.logging.ServerLogger;
import dev.nincodedo.ninbot.common.logging.ServerLoggerFactory;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Locale;
import java.util.ResourceBundle;

public class BaseListenerAdapter extends ListenerAdapter {

    protected ServerLogger log;

    public BaseListenerAdapter(ServerLoggerFactory serverLoggerFactory) {
        this.log = serverLoggerFactory.getLogger(this.getClass());
    }

    Locale defaultLocale = Locale.ENGLISH;

    protected ResourceBundle resourceBundle() {
        return resourceBundle(defaultLocale);
    }

    protected ResourceBundle resourceBundle(Locale locale) {
        return ResourceBundle.getBundle("lang", locale);
    }

    protected String resource(String resourceBundleKey) {
        return resourceBundle().getString(resourceBundleKey);
    }

    protected String resource(String resourceBundleKey, Locale locale) {
        return resourceBundle(locale).getString(resourceBundleKey);
    }
}
