package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.common.Constants;
import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class PollUserChoiceListener extends StatAwareListenerAdapter {

    private PollService pollService;
    private PollAnnouncementSetup pollAnnouncementSetup;
    private String pollMessageId;

    public PollUserChoiceListener(StatManager statManager, PollService pollService, String pollMessageId,
            PollAnnouncementSetup pollAnnouncementSetup) {
        super(statManager);
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
        if (!pollChoices.contains(strippedMessage) && pollChoices.size() < Constants.POLL_CHOICE_LIMIT) {
            poll.getChoices().add(strippedMessage);
            pollService.save(poll);
            refMessage.editMessage(poll.build()).queue();
            pollAnnouncementSetup.setupAnnounce(poll, channel.getJDA().getShardManager(), refMessage);
        } else {
            message.addReaction(Emojis.CROSS_X).queue();
        }
    }
}
