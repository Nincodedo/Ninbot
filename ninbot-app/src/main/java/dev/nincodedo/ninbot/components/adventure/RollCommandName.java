package dev.nincodedo.ninbot.components.adventure;

import dev.nincodedo.nincord.command.CommandNameEnum;

enum RollCommandName implements CommandNameEnum {
    ROLL;

    enum Option implements CommandNameEnum {
        NOTATION
    }
}
