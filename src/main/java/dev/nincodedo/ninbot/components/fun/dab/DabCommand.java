package dev.nincodedo.ninbot.components.fun.dab;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.command.SlashCommand;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.reaction.EmojiReactionResponse;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
public class DabCommand extends AbstractCommand implements SlashCommand {

    private static final int MESSAGE_SEARCH_LIMIT = 10;
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
        var content = event.getMessage().getContentStripped();
        if (isCommandLengthCorrect(content)) {
            doDabarinos(event.getJDA()
                    .getShardManager(), event.getChannel(), event.getMessage(), event.getAuthor(), messageAction, null);
        } else {
            messageAction.addUnknownReaction();
        }
        return messageAction;
    }

    private void doDabarinos(ShardManager shardManager, MessageChannel channel, Message eventMessage,
            User eventMessageAuthor, MessageAction messageAction, User dabbedOn) {
        var mentionedUsers = eventMessage.getMentionedUsers();
        var dabUser = dabbedOn == null ? mentionedUsers.get(mentionedUsers.size() - 1) : dabbedOn;
        for (Message message : channel.getHistoryBefore(eventMessage, MESSAGE_SEARCH_LIMIT)
                .complete()
                .getRetrievedHistory()) {
            if (message.getAuthor().equals(dabUser)) {
                messageAction.setOverrideMessage(message);
                dabOnMessage(messageAction, shardManager, eventMessageAuthor);
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

        var critInt = random.nextInt(100);
        var critDab = critInt < dabCritPercentChance;
        if (critDab) {
            messageAction.addReaction(critResponse.getEmojiList());
            messageAction.addReaction(dabResponse.getEmojiList());
        }

        var list = shardManager.getEmotes().stream()
                .filter(emote -> emote.getName().contains("dab"))
                .collect(Collectors.toList());

        Collections.shuffle(list);

        List<String> emoteNameList = new ArrayList<>();
        List<Emote> emoteList = new ArrayList<>();
        for (var emote : list) {
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

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(new OptionData(OptionType.USER, "dabbed", "a poor soul", true));
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        MessageAction messageAction = new MessageAction();
        //TODO what the hell is this, please fix it
        doDabarinos(slashCommandEvent.getJDA()
                        .getShardManager(), slashCommandEvent.getChannel(), slashCommandEvent.getChannel()
                        .getIterableHistory()
                        .complete()
                        .get(0), slashCommandEvent.getUser(), messageAction,
                slashCommandEvent.getOptionsByType(OptionType.USER)
                        .get(0)
                        .getAsUser());
        messageAction.executeActions();
        slashCommandEvent.reply(new MessageBuilder().append(slashCommandEvent.getGuild()
                .getEmotesByName("ninbotdab", true)
                .get(0))
                .append(" ")
                .append(slashCommandEvent.getOptionsByType(OptionType.USER).get(0).getAsUser())
                .build())
                .queue();
    }
}
