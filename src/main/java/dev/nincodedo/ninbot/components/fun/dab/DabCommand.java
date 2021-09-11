package dev.nincodedo.ninbot.components.fun.dab;

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
import java.util.stream.Collectors;

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
        return "dab";
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        MessageExecutor<SlashCommandEventMessageExecutor> messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        //TODO what the hell is this, please fix it
        doDabarinos(slashCommandEvent.getJDA()
                        .getShardManager(), slashCommandEvent.getMessageChannel(), slashCommandEvent.getUser(),
                messageExecutor,
                slashCommandEvent.getOption("dabbed").getAsUser());
        messageExecutor.executeActions();
        slashCommandEvent.reply(new MessageBuilder().append(slashCommandEvent.getGuild()
                                .getEmotesByName("ninbotdab", true)
                                .get(0))
                        .append(" ")
                        .append(slashCommandEvent.getOption("dabbed").getAsUser())
                        .build())
                .queue();
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

    private void dabOnMessage(MessageExecutor<SlashCommandEventMessageExecutor> messageExecutor, ShardManager shardManager,
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
                .collect(Collectors.toList());

        log.trace("Dabbing from {} potential dabs", emoteList.size());
        sendDabs(messageExecutor, emoteList);
    }

    void sendDabs(MessageExecutor<SlashCommandEventMessageExecutor> messageExecutor, List<Emote> emoteList) {
        messageExecutor.addReactionEmotes(emoteList.stream()
                .limit(20)
                .collect(Collectors.toList()));
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(new OptionData(OptionType.USER, "dabbed", "the poor soul.", true));
    }
}
