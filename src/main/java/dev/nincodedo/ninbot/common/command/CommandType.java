package dev.nincodedo.ninbot.common.command;

import org.apache.commons.lang.WordUtils;

public enum CommandType {
    SLASH, MESSAGE, USER, AUTO_COMPLETE, BUTTON, MODAL;


    @Override
    public String toString() {
        String s = super.toString().replace('_', ' ').toLowerCase();
        return WordUtils.capitalize(s).replace(" ", "");
    }
}
