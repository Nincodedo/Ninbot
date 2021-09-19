package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.ninbot.common.command.CommandNameEnum;

enum CountdownCommandName implements CommandNameEnum {
    COUNTDOWN;

    enum Subcommand implements CommandNameEnum {
        LIST, CREATE
    }

    enum Option implements CommandNameEnum {
        NAME, MONTH, DAY, YEAR
    }
}
