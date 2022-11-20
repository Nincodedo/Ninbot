package dev.nincodedo.ninbot.components.subscribe;

import dev.nincodedo.nincord.command.CommandNameEnum;

enum SubscribeCommandName implements CommandNameEnum {
    SUBSCRIBE, UNSUBSCRIBE;

    enum Option implements CommandNameEnum {
        SUBSCRIPTION
    }
}
