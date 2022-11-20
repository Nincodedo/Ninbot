package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.nincord.command.CommandNameEnum;

enum PollCommandName implements CommandNameEnum {
    POLL;

    enum Option implements CommandNameEnum {
        QUESTION, CHOICE1, CHOICE2, CHOICE3, CHOICE4, CHOICE5, CHOICE6, CHOICE7, CHOICE8, CHOICE9, POLLLENGTH,
        USERCHOICES, CHOICE
    }
}
