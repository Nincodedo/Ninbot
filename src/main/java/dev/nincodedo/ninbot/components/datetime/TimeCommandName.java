package dev.nincodedo.ninbot.components.datetime;

import dev.nincodedo.ninbot.common.command.CommandNameEnum;

enum TimeCommandName implements CommandNameEnum {
    TIME;

    enum Option implements CommandNameEnum {
        AMOUNT, UNIT, DISPLAY
    }
}
