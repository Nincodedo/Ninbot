package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.reaction.EmojiReactionResponse;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
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
        val content = event.getMessage().getContentStripped();
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
                    messageUtils.reactUnsuccessfulResponse(event.getMessage());
                    break;
                }
                count++;
            }
        } else {
            messageUtils.reactUnsuccessfulResponse(event.getMessage());
        }
    }

    private void dabOnMessage(Message message, MessageChannel channel) {
        val guild = message.getGuild();
        Random random = new Random();
        val critDab = random.nextInt(100) < 5;
        if (critDab) {
            critResponse.react(message, channel, messageUtils);
        }
        guild.getEmotes().stream().filter(emote -> emote.getName().contains("dab")).forEachOrdered(emote -> messageUtils.addReaction(message, emote));
        if (critDab) {
            dabResponse.react(message, channel, messageUtils);
        }
    }
}
