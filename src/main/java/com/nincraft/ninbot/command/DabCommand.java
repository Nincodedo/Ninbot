package com.nincraft.ninbot.command;

import com.nincraft.ninbot.response.EmojiReactionResponse;
import com.nincraft.ninbot.util.MessageUtils;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Random;

public class DabCommand extends AbstractCommand {

    private EmojiReactionResponse critResponse = new EmojiReactionResponse("crit");
    private EmojiReactionResponse dabResponse = new EmojiReactionResponse("dab");

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
            val mentionedUsers = event.getMessage().getMentionedUsers();
            val dabUser = mentionedUsers.get(mentionedUsers.size() - 1);
            int count = 0;
            int maxDab = 10;
            for (Message message : channel.getIterableHistory()) {
                if (message.getAuthor().equals(dabUser)) {
                    dabOnMessage(message, channel);
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

    private void dabOnMessage(Message message, MessageChannel channel) {
        val guild = message.getGuild();
        Random random = new Random();
        val critDab = random.nextInt(100) < 5;
        if (critDab) {
            critResponse.react(message, channel);
        }
        for (val emote : guild.getEmotes()) {
            if (emote.getName().contains("dab")) {
                MessageUtils.addReaction(message, emote);
            }
        }
        if (critDab) {
            dabResponse.react(message, channel);
        }
    }
}
