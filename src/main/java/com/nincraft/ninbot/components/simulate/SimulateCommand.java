package com.nincraft.ninbot.components.simulate;

import com.nincraft.ninbot.components.command.CooldownCommand;
import com.nincraft.ninbot.components.common.MessageAction;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Log4j2
@Component
public class SimulateCommand extends CooldownCommand {

    private static final String WEBHOOK_NAME = "ninbot";
    private Random random;

    public SimulateCommand() {
        name = "simulate";
        length = 3;
        cooldownUnit = ChronoUnit.MINUTES;
        cooldownValue = 5;
        this.random = new Random();
    }

    @Override
    protected MessageAction executeCooldownCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val mentionedUsers = event.getMessage().getMentionedUsers();
        val targetUser = mentionedUsers.get(mentionedUsers.size() - 1);
        List<Message> userMessages;
        try {
             userMessages = getUserMessages(event.getGuild(), targetUser);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to run user simulation", e);
            messageAction.addUnsuccessfulReaction();
            return messageAction;
        }
        if (userMessages.isEmpty()) {
            return messageAction;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int loopLength = random.nextInt(3) + 3;
        for (int i = 0; i < loopLength; i++) {
            val message = userMessages.get(random.nextInt(userMessages.size())).getContentStripped() + " ";
            stringBuilder.append(message);
        }
        val webhookOptional = webhookHelper.getWebhookByName(event.getGuild(), event.getTextChannel(),
                WEBHOOK_NAME);
        val member = event.getGuild().getMember(targetUser);
        if (webhookOptional.isPresent()) {
            val webhook = webhookOptional.get();
            val manager = webhook.getManager();
            webhookHelper.setWebhookIcon(targetUser.getEffectiveAvatarUrl(), manager);
            manager.setName(member.getEffectiveName()).queue(aVoid -> {
                webhookHelper.sendMessage(stringBuilder.toString(), webhook.getUrl());
                manager.setName(WEBHOOK_NAME).queue();
            });
        } else {
            messageAction.addChannelAction(new EmbedBuilder().appendDescription(stringBuilder.toString()));
        }


        return messageAction;
    }

    private List<Message> getUserMessages(Guild guild, User user) throws ExecutionException, InterruptedException {
        List<Message> returnMessages = new ArrayList<>();
        //Limit to 10 random channels that the default public role can read/talk
        val textChannels = guild.getTextChannels().parallelStream()
                .filter(textChannel -> {
                    val membersWithPublicRole = guild.getMembersWithRoles(guild.getPublicRole());
                    if (!membersWithPublicRole.isEmpty()) {
                        return textChannel.canTalk(membersWithPublicRole.get(0));
                    }
                    return false;
                })
                .unordered()
                .limit(10)
                .collect(Collectors.toList());
        for (val textChannel : textChannels) {
            //Limit to 1000 messages from those channels where the target is the message author, they're not
            //mentioning Ninbot and their post isn't blank (really only relevant to bots)
            CompletableFuture<List<Message>> apply = textChannel.getIterableHistory()
                    .takeAsync(1000)
                    .thenApply(messages ->
                            messages.parallelStream()
                                    .filter(message -> message.getAuthor().equals(user))
                                    .filter(message -> !message.getContentStripped().toLowerCase().contains("@ninbot"))
                                    .filter(message -> !message.getContentStripped().trim().isBlank())
                                    .collect(Collectors.toList())
                    );
            returnMessages.addAll(apply.get());
        }

        return returnMessages;
    }
}
