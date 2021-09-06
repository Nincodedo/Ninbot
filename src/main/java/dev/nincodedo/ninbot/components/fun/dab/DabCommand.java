package dev.nincodedo.ninbot.components.fun.dab;

import dev.nincodedo.ninbot.common.StreamUtils;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageReceivedEventMessageAction;
import dev.nincodedo.ninbot.components.reaction.EmojiReactionResponse;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
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

    private boolean isDabEdition(String commitId) {
        return commitId != null && commitId.toLowerCase().contains("dab");
    }

    private void doDabarinos(ShardManager shardManager, MessageChannel messageChannel,
            User eventMessageAuthor, MessageReceivedEventMessageAction messageAction, User dabbedOn) {
        var eventMessageOptional = messageChannel.getIterableHistory()
                .stream()
                .limit(10)
                .filter(message -> message.getAuthor().equals(dabbedOn))
                .findFirst();
        if (eventMessageOptional.isPresent()) {
            messageAction.setOverrideMessage(eventMessageOptional.get());
            dabOnMessage(messageAction, shardManager, eventMessageAuthor);
            return;
        }
        messageAction.addUnsuccessfulReaction();
    }

    private void dabOnMessage(MessageReceivedEventMessageAction messageAction, ShardManager shardManager,
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
            messageAction.addReaction(critResponse.getEmojiList());
            messageAction.addReaction(dabResponse.getEmojiList());
        }

        var emoteList = shardManager.getEmotes().stream()
                .filter(emote -> emote.getName().contains("dab"))
                .sorted(StreamUtils.shuffle())
                .distinct()
                .collect(Collectors.toList());

        log.trace("Dabbing from {} potential dabs", emoteList.size());
        sendDabs(messageAction, emoteList);
    }

    void sendDabs(MessageReceivedEventMessageAction messageAction, List<Emote> emoteList) {
        messageAction.addReactionEmotes(emoteList.stream()
                .limit(20)
                .collect(Collectors.toList()));
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(new OptionData(OptionType.USER, "dabbed", "the poor soul.", true));
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        MessageReceivedEventMessageAction messageAction = new MessageReceivedEventMessageAction();
        //TODO what the hell is this, please fix it
        doDabarinos(slashCommandEvent.getJDA()
                        .getShardManager(), slashCommandEvent.getMessageChannel(), slashCommandEvent.getUser(),
                messageAction,
                slashCommandEvent.getOption("dabbed").getAsUser());
        messageAction.executeActions();
        slashCommandEvent.reply(new MessageBuilder().append(slashCommandEvent.getGuild()
                                .getEmotesByName("ninbotdab", true)
                                .get(0))
                        .append(" ")
                        .append(slashCommandEvent.getOption("dabbed").getAsUser())
                        .build())
                .queue();
    }
}
