package dev.nincodedo.ninbot.components.tally;

import dev.nincodedo.ninbot.common.command.CommandNameEnum;

enum TallyCommandName implements CommandNameEnum {
    TALLY;

    enum Subcommand implements CommandNameEnum {
        ADD, GET
    }

    enum Option implements CommandNameEnum {
        NAME, COUNT
    }
}
