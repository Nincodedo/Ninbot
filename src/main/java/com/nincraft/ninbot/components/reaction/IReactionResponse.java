package com.nincraft.ninbot.components.reaction;

import com.nincraft.ninbot.components.common.MessageUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

interface IReactionResponse {

    void react(Message message, MessageChannel channel, MessageUtils messageUtils);
}
