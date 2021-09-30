package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.ninbot.common.command.CommandNameEnum;

enum UserCommandName implements CommandNameEnum {
    USER;

    enum Subcommand implements CommandNameEnum {
        BIRTHDAY, ANNOUNCEMENT
    }

    enum Option implements CommandNameEnum {
        MONTH, DAY
    }
}
