package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.MessageUtils;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DabCommand extends AbstractCommand {

    public DabCommand() {
        length = 3;
        name = "dab";
        description = "Adds all dab emojis to the last message of the user named";
        checkExactLength = false;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        val content = event.getMessage().getContent();
        if (isCommandLengthCorrect(content)) {
            val channel = event.getChannel();
            val dabUser = content.substring(12).replaceFirst("@", "");
            int count = 0;
            int maxDab = 10;
            for (Message message : channel.getIterableHistory()) {
                if (message.getAuthor().getName().equalsIgnoreCase(dabUser)) {
                    dabOnMessage(message);
                    break;
                }
                if (count >= maxDab) {
                    MessageUtils.reactUnsuccessfulResponse(event.getMessage());
                    break;
                }
                count++;
            }
        } else {
            MessageUtils.reactUnsuccessfulResponse(event.getMessage());
        }
    }

    private void dabOnMessage(Message message) {
        val guild = message.getGuild();

        for (val emote : guild.getEmotes()) {
            if (emote.getName().contains("dab")) {
                MessageUtils.addReaction(message, emote);
            }
        }
    }
}
