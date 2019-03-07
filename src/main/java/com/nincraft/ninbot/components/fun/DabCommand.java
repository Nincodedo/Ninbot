package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.reaction.EmojiReactionResponse;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.stream.Collectors;

@Component
public class DabCommand extends AbstractCommand {

    private EmojiReactionResponse critResponse = new EmojiReactionResponse("crit");
    private EmojiReactionResponse dabResponse = new EmojiReactionResponse("dab");
    private SecureRandom random;

    public DabCommand() {
        length = 3;
        name = "dab";
        description = "Adds all dab emojis to the last message of the user named";
        checkExactLength = false;
        random = new SecureRandom();
    }

    @Override
    public CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val content = event.getMessage().getContentStripped();
        if (isCommandLengthCorrect(content)) {
            val channel = event.getChannel();
            val mentionedUsers = event.getMessage().getMentionedUsers();
            val dabUser = mentionedUsers.get(mentionedUsers.size() - 1);
            int count = 0;
            int maxDab = 10;
            for (Message message : channel.getIterableHistory()) {
                if (message.getAuthor().equals(dabUser)) {
                    dabOnMessage(commandResult, message.getGuild());
                    break;
                }
                if (count >= maxDab) {
                    commandResult.addUnsuccessfulReaction();
                    break;
                }
                count++;
            }
        } else {
            commandResult.addUnknownReaction();
        }
        return commandResult;
    }

    private void dabOnMessage(CommandResult commandResult, Guild guild) {
        val critDab = random.nextInt(100) < 5;
        if (critDab) {
            commandResult.addReaction(critResponse.getEmojiList());
            commandResult.addReaction(dabResponse.getEmojiList());
        }
        commandResult.addReactionEmotes(guild.getEmotes().stream().filter(emote -> emote.getName().contains("dab")).collect(Collectors.toList()));
    }
}
