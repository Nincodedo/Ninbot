package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.command.CommandNameEnum;

enum StreamCommandName implements CommandNameEnum {
    STREAM;

    enum Button implements CommandNameEnum {
        NOTHING, TOGGLE
    }
}
