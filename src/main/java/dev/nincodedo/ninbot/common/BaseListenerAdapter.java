package dev.nincodedo.ninbot.common;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class BaseListenerAdapter extends ListenerAdapter {

    Locale defaultLocale = Locale.ENGLISH;

    ResourceBundle resourceBundle() {
        return resourceBundle(defaultLocale);
    }

    ResourceBundle resourceBundle(Locale locale) {
        return ResourceBundle.getBundle("lang", locale);
    }

    String resource(String resourceBundleKey) {
        return resourceBundle().getString(resourceBundleKey);
    }

    String resource(String resourceBundleKey, Locale locale) {
        return resourceBundle(locale).getString(resourceBundleKey);
    }
}
