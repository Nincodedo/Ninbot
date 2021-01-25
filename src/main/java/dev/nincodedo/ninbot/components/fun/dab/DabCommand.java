package dev.nincodedo.ninbot.components.fun.dab;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.reaction.EmojiReactionResponse;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
public class DabCommand extends AbstractCommand {

    private EmojiReactionResponse critResponse = new EmojiReactionResponse("crit");
    private EmojiReactionResponse dabResponse = new EmojiReactionResponse("dab");
    private SecureRandom random;
    private boolean isDabEdition;

    public DabCommand(GitProperties gitProperties) {
        length = 3;
        name = "dab";
        checkExactLength = false;
        random = new SecureRandom();
        isDabEdition = isDabEdition(gitProperties.getCommitId());
    }

    private boolean isDabEdition(String commitId) {
        return commitId != null && commitId.toLowerCase().contains("dab");
    }

    @Override
    public MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val content = event.getMessage().getContentStripped();
        if (isCommandLengthCorrect(content)) {
            doDabarinos(event, messageAction);
        } else {
            messageAction.addUnknownReaction();
        }
        return messageAction;
    }

    private void doDabarinos(MessageReceivedEvent event, MessageAction messageAction) {
        val channel = event.getChannel();
        val mentionedUsers = event.getMessage().getMentionedUsers();
        val dabUser = mentionedUsers.get(mentionedUsers.size() - 1);
        for (Message message : channel.getHistoryBefore(event.getMessage(), 10).complete().getRetrievedHistory()) {
            if (message.getAuthor().equals(dabUser)) {
                messageAction.setOverrideMessage(message);
                dabOnMessage(messageAction, event.getJDA().getShardManager(), event.getAuthor());
                return;
            }
        }
        messageAction.addUnsuccessfulReaction();
    }

    private void dabOnMessage(MessageAction messageAction, ShardManager shardManager, User commandUser) {
        int dabCritPercentChance = 5;

        if (isUserNinbotSupporter(shardManager, commandUser)) {
            dabCritPercentChance = dabCritPercentChance * 2;
            log.trace("Made possible by Patreon");
        }
        if (isDabEdition) {
            dabCritPercentChance = dabCritPercentChance * 2;
        }

        val critInt = random.nextInt(100);
        val critDab = critInt < dabCritPercentChance;
        if (critDab) {
            messageAction.addReaction(critResponse.getEmojiList());
            messageAction.addReaction(dabResponse.getEmojiList());
        }

        val list = shardManager.getEmotes().stream()
                .filter(emote -> emote.getName().contains("dab"))
                .collect(Collectors.toList());

        Collections.shuffle(list);

        List<String> emoteNameList = new ArrayList<>();
        List<Emote> emoteList = new ArrayList<>();
        for (val emote : list) {
            if (!emoteNameList.contains(emote.getName())) {
                emoteNameList.add(emote.getName());
                emoteList.add(emote);
            }
        }

        log.trace("Dabbing from {} potential dabs", emoteList.size());

        sendDabs(messageAction, emoteList);
    }

    void sendDabs(MessageAction messageAction, List<Emote> emoteList) {
        messageAction.addReactionEmotes(emoteList.stream()
                .limit(20)
                .collect(Collectors.toList()));
    }
}
