package dev.nincodedo.ninbot.components.dab;

import dev.nincodedo.ninbot.common.Constants;
import dev.nincodedo.ninbot.common.StreamUtils;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import dev.nincodedo.ninbot.components.reaction.EmojiReactionResponse;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class DabCommand implements SlashCommand {

    private EmojiReactionResponse critResponse = new EmojiReactionResponse("crit");
    private EmojiReactionResponse dabResponse = new EmojiReactionResponse("dab");
    private SecureRandom random;
    private boolean isDabEdition;

    public DabCommand(GitProperties gitProperties) {
        random = new SecureRandom();
        isDabEdition = isDabEdition(gitProperties.getCommitId());
    }

    @Override
    public String getName() {
        return DabCommandName.DAB.get();
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(SlashCommandEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        doDabarinos(slashCommandEvent.getJDA()
                        .getShardManager(), slashCommandEvent.getMessageChannel(), slashCommandEvent.getUser(),
                messageExecutor,
                slashCommandEvent.getOption(DabCommandName.Option.DABBED.get()).getAsUser());

        messageExecutor.addMessageResponse(new MessageBuilder().append(slashCommandEvent.getJDA()
                        .getGuildById(Constants.OCW_SERVER_ID)
                        .getEmotesByName("ninbotdab", true)
                        .get(0))
                .append(" ")
                .append(slashCommandEvent.getOption(DabCommandName.Option.DABBED.get()).getAsUser())
                .build());
        return messageExecutor;
    }

    private boolean isDabEdition(String commitId) {
        return commitId != null && commitId.toLowerCase().contains("dab");
    }

    private void doDabarinos(ShardManager shardManager, MessageChannel messageChannel,
            User eventMessageAuthor, MessageExecutor<SlashCommandEventMessageExecutor> messageExecutor, User dabbedOn) {
        var eventMessageOptional = messageChannel.getIterableHistory()
                .stream()
                .limit(10)
                .filter(message -> message.getAuthor().equals(dabbedOn))
                .findFirst();
        if (eventMessageOptional.isPresent()) {
            messageExecutor.setOverrideMessage(eventMessageOptional.get());
            dabOnMessage(messageExecutor, shardManager, eventMessageAuthor);
            return;
        }
        messageExecutor.addUnsuccessfulReaction();
    }

    private void dabOnMessage(MessageExecutor<SlashCommandEventMessageExecutor> messageExecutor,
            ShardManager shardManager,
            User commandUser) {
        int dabCritPercentChance = 5;

        if (isUserNinbotSupporter(shardManager, commandUser)) {
            dabCritPercentChance = dabCritPercentChance * 2;
            log.trace("Made possible by Patreon");
        }
        if (isDabEdition) {
            dabCritPercentChance = dabCritPercentChance * 2;
        }

        var critInt = random.nextInt(100);
        var critDab = critInt < dabCritPercentChance;
        if (critDab) {
            messageExecutor.addReaction(critResponse.getEmojiList());
            messageExecutor.addReaction(dabResponse.getEmojiList());
        }

        var emoteList = shardManager.getEmotes().stream()
                .filter(emote -> emote.getName().contains("dab"))
                .sorted(StreamUtils.shuffle())
                .distinct()
                .toList();

        log.trace("Dabbing from {} potential dabs", emoteList.size());
        sendDabs(messageExecutor, emoteList);
    }

    void sendDabs(MessageExecutor<SlashCommandEventMessageExecutor> messageExecutor, List<Emote> emoteList) {
        messageExecutor.addReactionEmotes(emoteList.stream()
                .limit(20)
                .toList());
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(new OptionData(OptionType.USER, DabCommandName.Option.DABBED.get(), "the poor soul.",
                true));
    }
}
