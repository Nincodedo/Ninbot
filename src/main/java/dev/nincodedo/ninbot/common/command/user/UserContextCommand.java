package dev.nincodedo.ninbot.common.command.user;

import dev.nincodedo.ninbot.common.command.Command;
import dev.nincodedo.ninbot.common.message.UserContextInteractionEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public interface UserContextCommand extends Command<UserContextInteractionEventMessageExecutor,
        UserContextInteractionEvent> {
}
