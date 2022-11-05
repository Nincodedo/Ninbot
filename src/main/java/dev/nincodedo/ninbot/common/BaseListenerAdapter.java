package dev.nincodedo.ninbot.common;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class BaseListenerAdapter extends ListenerAdapter {

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

}
