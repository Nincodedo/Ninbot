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
        val guilds = targetUser.getMutualGuilds();

        try {
            val list = getUserMessages(guilds, targetUser);
            if (list.isEmpty()) {
                return messageAction;
            }
            StringBuilder stringBuilder = new StringBuilder();
            int loopLength = random.nextInt(3) + 3;
            for (int i = 0; i < loopLength; i++) {
                val message = list.get(random.nextInt(list.size())).getContentStripped() + " ";
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
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to run user simulation", e);
            messageAction.addUnsuccessfulReaction();
        }

        return messageAction;
    }

    private List<Message> getUserMessages(List<Guild> guilds,
            User user) throws ExecutionException, InterruptedException {
        List<Message> returnMessages = new ArrayList<>();
        for (val guild : guilds) {
            for (val textChannel : guild.getTextChannels()) {
                val apply = textChannel.getIterableHistory().takeAsync(1000).thenApply(messages ->
                        messages.parallelStream()
                                .filter(message -> message.getAuthor().equals(user))
                                .filter(message -> !message.getContentStripped().toLowerCase().contains("@ninbot"))
                                .filter(message -> !message.getContentStripped().trim().isBlank())
                                .collect(Collectors.toList())
                );
                returnMessages.addAll((List<Message>) apply.get());
            }
        }
        return returnMessages;
    }
}
