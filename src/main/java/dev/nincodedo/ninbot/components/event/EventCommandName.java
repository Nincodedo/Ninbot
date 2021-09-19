package dev.nincodedo.ninbot.components.event;

import dev.nincodedo.ninbot.common.command.CommandNameEnum;

enum EventCommandName implements CommandNameEnum {
    EVENT;
    enum Subcommand implements CommandNameEnum {
        LIST, PLAN
    }
    enum Option implements CommandNameEnum {
        NAME, DATE, GAME, TIME
    }
}
