package dev.nincodedo.ninbot.components.eightball;

import dev.nincodedo.ninbot.common.command.CommandNameEnum;

enum Magic8BallCommandName implements CommandNameEnum {
    EIGHTBALL;

    enum Option implements CommandNameEnum {
        QUESTION
    }
}
