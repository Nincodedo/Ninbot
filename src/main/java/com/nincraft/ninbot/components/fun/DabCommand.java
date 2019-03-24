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
        checkExactLength = false;
        random = new SecureRandom();
    }

    @Override
    public CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val content = event.getMessage().getContentStripped();
        if (isCommandLengthCorrect(content)) {
            doDabarinos(event, commandResult);
        } else {
            commandResult.addUnknownReaction();
        }
        return commandResult;
    }

    private void doDabarinos(MessageReceivedEvent event, CommandResult commandResult) {
        val channel = event.getChannel();
        val mentionedUsers = event.getMessage().getMentionedUsers();
        val dabUser = mentionedUsers.get(mentionedUsers.size() - 1);
        for (Message message : channel.getHistoryBefore(event.getMessage(), 10).complete().getRetrievedHistory()) {
            if (message.getAuthor().equals(dabUser)) {
                commandResult.setOverrideMessage(message);
                dabOnMessage(commandResult, message.getGuild());
                return;
            }
        }
        commandResult.addUnsuccessfulReaction();
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
