package dev.nincodedo.ninbot.components.define;

import dev.nincodedo.ninbot.common.command.CommandNameEnum;

enum DefineCommandName implements CommandNameEnum {
    DEFINE;

    enum Option implements CommandNameEnum {
        WORD
    }
}
