package dev.nincodedo.ninbot.components.simulate;

import dev.nincodedo.ninbot.components.command.CooldownCommand;
import dev.nincodedo.ninbot.components.common.message.Impersonation;
import dev.nincodedo.ninbot.components.common.message.ImpersonationController;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
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

        val member = event.getGuild().getMember(targetUser);
        ImpersonationController impersonationController =
                new ImpersonationController(Impersonation.of(member.getEffectiveName(), targetUser
                        .getEffectiveAvatarUrl()), event.getGuild(), event.getTextChannel());
        impersonationController.sendMessage(stringBuilder.toString());
        return messageAction;
    }

    private List<Message> getUserMessages(Guild guild, User user) throws ExecutionException, InterruptedException {
        List<Message> returnMessages = new ArrayList<>();
        //Limit to 10 random channels that the default public role can read/talk
        Member memberWithLeastPermissions = getMemberWithLeastPermissions(guild);
        if (memberWithLeastPermissions == null) {
            return new ArrayList<>();
        }
        val guildChannels = guild.getChannels(false).parallelStream()
                .filter(guildChannel -> guildChannel.getType().equals(ChannelType.TEXT))
                .filter(memberWithLeastPermissions::hasAccess)
                .unordered()
                .limit(10)
                .collect(Collectors.toList());
        for (val guildChannel : guildChannels) {
            //Limit to 1000 messages from those channels where the target is the message author, they're not
            //mentioning Ninbot and their post isn't blank (really only relevant to bots)
            TextChannel textChannel = (TextChannel) guildChannel;
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

    private Member getMemberWithLeastPermissions(Guild guild) {
        Member memberWithLeastPermissions = null;
        long count = Long.MAX_VALUE;
        for (val member : guild.getMembersWithRoles(Collections.emptyList())) {
            if (member.getPermissions().size() < count) {
                count = Permission.getRaw(member.getPermissions());
                memberWithLeastPermissions = member;
            }
        }
        return memberWithLeastPermissions;
    }
}
