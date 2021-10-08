package dev.nincodedo.ninbot.components.dab;

import dev.nincodedo.ninbot.common.command.CommandNameEnum;

enum DabCommandName implements CommandNameEnum {
    DAB, HUGEDAB;

    enum Option implements CommandNameEnum {
        DABBED
    }
}
