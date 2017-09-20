package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.MessageUtils;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;

public class UnsubscribeCommand extends SubscribeCommand {
    public UnsubscribeCommand() {
        super();
        name = "unsubscribe";
        description = "Unsubscribes you from a game for game gathering events";
    }

    @Override
    void addOrRemoveSubscription(MessageReceivedEvent event, MessageChannel channel, GuildController controller, String subscribeTo, Role role) {
        MessageUtils.reactSuccessfulResponse(event.getMessage());
        controller.removeRolesFromMember(event.getMember(), role).queue();
    }
}
