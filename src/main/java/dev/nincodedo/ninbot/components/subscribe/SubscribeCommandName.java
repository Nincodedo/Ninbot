package dev.nincodedo.ninbot.components.subscribe;

import dev.nincodedo.ninbot.common.command.CommandNameEnum;

enum SubscribeCommandName implements CommandNameEnum {
    SUBSCRIBE, UNSUBSCRIBE;

    enum Option implements CommandNameEnum {
        SUBSCRIPTION
    }
}
