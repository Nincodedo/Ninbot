package com.nincraft.ninbot.components.reaction;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public interface IReactionResponse {

    void addReaction(String reaction);

    void react(Message message, MessageChannel channel);
}
