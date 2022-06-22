package dev.nincodedo.ninbot.common.command.slash.info;

import dev.nincodedo.ninbot.common.command.CommandNameEnum;

enum InfoCommandName implements CommandNameEnum {
    INFO;

    enum Option implements CommandNameEnum {
        EXTRA
    }
}
