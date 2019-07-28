package com.nincraft.ninbot.components.reaction;

import lombok.Data;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@Data
class ReactionResponse {

    protected String response;
    protected String target;
    protected String type;

    public void react(Message message, MessageChannel channel) {
        //NO-OP
    }
}
