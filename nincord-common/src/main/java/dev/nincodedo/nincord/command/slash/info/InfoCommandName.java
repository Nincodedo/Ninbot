package dev.nincodedo.nincord.command.slash.info;

import dev.nincodedo.nincord.command.CommandNameEnum;

enum InfoCommandName implements CommandNameEnum {
    INFO;

    enum Option implements CommandNameEnum {
        EXTRA
    }
}
