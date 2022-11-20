package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.nincord.command.CommandNameEnum;

enum StreamCommandName implements CommandNameEnum {
    STREAM;

    enum Button implements CommandNameEnum {
        NOTHING, TOGGLE, TWITCHNAME
    }
}
