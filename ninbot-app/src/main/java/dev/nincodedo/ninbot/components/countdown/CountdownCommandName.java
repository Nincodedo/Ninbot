package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.nincord.command.CommandNameEnum;

enum CountdownCommandName implements CommandNameEnum {
    COUNTDOWN;

    enum Subcommand implements CommandNameEnum {
        LIST, CREATE, DELETE
    }

    enum Option implements CommandNameEnum {
        NAME, MONTH, DAY, YEAR
    }
}
