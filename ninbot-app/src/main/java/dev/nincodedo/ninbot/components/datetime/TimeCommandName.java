package dev.nincodedo.ninbot.components.datetime;

import dev.nincodedo.nincord.command.CommandNameEnum;

enum TimeCommandName implements CommandNameEnum {
    TIME;

    enum Option implements CommandNameEnum {
        AMOUNT, UNIT, DISPLAY
    }
}
