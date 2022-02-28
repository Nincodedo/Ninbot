package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.release.ReleaseStage;

import java.util.Locale;

public interface Command extends ReleaseStage {
    Locale defaultLocale = Locale.ENGLISH;

    String getName();
}