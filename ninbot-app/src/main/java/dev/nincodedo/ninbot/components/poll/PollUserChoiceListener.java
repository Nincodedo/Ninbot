package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.nincord.Emojis;
import dev.nincodedo.nincord.StatAwareListenerAdapter;
import dev.nincodedo.nincord.stats.StatManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;

public class PollUserChoiceListener extends StatAwareListenerAdapter {

    private PollService pollService;
    private PollAnnouncementSetup pollAnnouncementSetup;
    private String pollMessageId;


    public PollUserChoiceListener(StatManager statManager, ExecutorService executorService, PollService pollService,
            String pollMessageId, PollAnnouncementSetup pollAnnouncementSetup) {
        super(statManager, executorService);
        this.pollService = pollService;
        this.pollMessageId = pollMessageId;
        this.pollAnnouncementSetup = pollAnnouncementSetup;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType().isGuild() && !event.getMessage().isWebhookMessage()) {
            var channel = (GuildMessageChannel) event.getChannel();
            onGuildMessageReceived(channel, event.getMessage());
        }
    }

    public void onGuildMessageReceived(GuildMessageChannel channel, Message message) {
        var refMessage = message.getReferencedMessage();
        var pollOptional = pollService.findByMessageIdAndPollOpen(pollMessageId, true);
        if (refMessage == null || !refMessage.getId().equals(pollMessageId) || pollOptional.isEmpty()
                || !pollOptional.get().isPollOpen() || !pollOptional.get()
                .isUserChoicesAllowed()) {
            return;
        }
        var strippedMessage = message.getContentStripped();
        var poll = pollOptional.get();
        var pollChoices = poll.getChoices();
        int pollChoiceLimit = 9;
        if (!pollChoices.contains(strippedMessage) && pollChoices.size() < pollChoiceLimit) {
            poll.getChoices().add(strippedMessage);
            pollService.save(poll);
            refMessage.editMessage(poll.editOpen()).queue();
            pollAnnouncementSetup.setupAnnounce(poll, channel.getJDA().getShardManager(), refMessage);
        } else {
            message.addReaction(Emoji.fromFormatted(Emojis.CROSS_X)).queue();
        }
    }
}
