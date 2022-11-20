package dev.nincodedo.ninbot.components.define;

import dev.nincodedo.nincord.command.CommandNameEnum;

enum DefineCommandName implements CommandNameEnum {
    DEFINE;

    enum Option implements CommandNameEnum {
        WORD
    }
}
