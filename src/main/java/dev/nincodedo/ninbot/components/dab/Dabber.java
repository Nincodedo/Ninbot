package dev.nincodedo.ninbot.components.dab;

import dev.nincodedo.ninbot.common.supporter.SupporterCheck;
import dev.nincodedo.ninbot.NinbotConstants;
import dev.nincodedo.ninbot.common.StreamUtils;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.components.reaction.EmojiReactionResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
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

    void dabOnMessage(MessageExecutor messageExecutor,
            ShardManager shardManager,
            User commandUser) {
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

        var emoteList = shardManager.getEmotes().stream()
                .filter(emote -> emote.getName().contains("dab"))
                .sorted(StreamUtils.shuffle())
                .distinct()
                .toList();

        log.trace("Dabbing from {} potential dabs", emoteList.size());
        sendDabs(messageExecutor, emoteList);
    }

    void sendDabs(MessageExecutor messageExecutor, List<Emote> emoteList) {
        messageExecutor.addReactionEmotes(emoteList.stream()
                .limit(20)
                .toList());
    }

    Message buildDabMessage(@NotNull JDA jda, @NotNull User target) {
        return new MessageBuilder().append(jda
                        .getGuildById(NinbotConstants.OCW_SERVER_ID)
                        .getEmotesByName("ninbotdab", true)
                        .get(0))
                .append(" ")
                .append(target)
                .build();
    }
}
