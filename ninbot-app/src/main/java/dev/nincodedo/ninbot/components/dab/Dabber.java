package dev.nincodedo.ninbot.components.dab;

import dev.nincodedo.ninbot.components.reaction.EmojiReactionResponse;
import dev.nincodedo.nincord.StreamUtils;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.supporter.SupporterCheck;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Component
public class Dabber {
    @Getter
    private SecureRandom random;
    private boolean isDabEdition;
    private SupporterCheck supporterCheck;
    @Getter
    private EmojiReactionResponse critResponse = new EmojiReactionResponse("crit");
    @Getter
    private EmojiReactionResponse dabResponse = new EmojiReactionResponse("dab");

    public Dabber(GitProperties gitProperties, SupporterCheck supporterCheck) {
        isDabEdition = isDabEdition(gitProperties.getCommitId());
        this.supporterCheck = supporterCheck;
        random = new SecureRandom();
    }

    private boolean isDabEdition(String commitId) {
        return commitId != null && commitId.toLowerCase().contains("dab");
    }

    void dabOnMessage(MessageExecutor messageExecutor, ShardManager shardManager, User commandUser) {
        int dabCritPercentChance = 5;

        if (supporterCheck.isSupporter(shardManager, commandUser)) {
            dabCritPercentChance = dabCritPercentChance * 2;
        }
        if (isDabEdition) {
            dabCritPercentChance = dabCritPercentChance * 2;
        }

        var critInt = getRandom().nextInt(100);
        var critDab = critInt < dabCritPercentChance;
        if (critDab) {
            messageExecutor.addReaction(getCritResponse().getEmojiList());
            messageExecutor.addReaction(getDabResponse().getEmojiList());
        }

        var emoteList = shardManager.getEmojis().stream()
                .filter(emoji -> emoji.getName().contains("dab"))
                .sorted(StreamUtils.shuffle())
                .distinct()
                .toList();

        log.trace("Dabbing from {} potential dabs", emoteList.size());
        sendDabs(messageExecutor, emoteList);
    }

    void sendDabs(MessageExecutor messageExecutor, List<RichCustomEmoji> emoteList) {
        messageExecutor.addReactionEmotes(emoteList.stream()
                .limit(20L - messageExecutor.getMessage().getReactions().size())
                .toList());
    }

    MessageCreateData buildDabMessage(@NotNull User target) {
        return new MessageCreateBuilder().addContent("<:ninbotdab:786750382771535902>")
                .addContent(" ")
                .addContent(target.getName())
                .build();
    }
}
