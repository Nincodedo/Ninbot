package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.nincord.command.CommandNameEnum;

enum UserCommandName implements CommandNameEnum {
    USER;

    enum Subcommand implements CommandNameEnum {
        FEATURES
    }
}
