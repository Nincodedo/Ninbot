package dev.nincodedo.ninbot.components.adventure;

import dev.nincodedo.ninbot.common.command.CommandNameEnum;

enum RollCommandName implements CommandNameEnum {
    ROLL;

    enum Option implements CommandNameEnum {
        NOTATION
    }
}
