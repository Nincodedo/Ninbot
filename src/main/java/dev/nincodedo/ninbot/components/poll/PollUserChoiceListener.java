package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.common.Constants;
import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PollUserChoiceListener extends StatAwareListenerAdapter {

    private PollRepository pollRepository;
    private PollAnnouncementSetup pollAnnouncementSetup;
    private String pollMessageId;

    public PollUserChoiceListener(StatManager statManager, PollRepository pollRepository, String pollMessageId,
            PollAnnouncementSetup pollAnnouncementSetup) {
        super(statManager);
        this.pollRepository = pollRepository;
        this.pollMessageId = pollMessageId;
        this.pollAnnouncementSetup = pollAnnouncementSetup;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        var refMessage = event.getMessage().getReferencedMessage();
        if (refMessage != null && refMessage.getId().equals(pollMessageId)) {
            var pollOptional = pollRepository.findByMessageIdAndPollOpen(pollMessageId, true);
            if (pollOptional.isPresent() && pollOptional.get().isPollOpen() && pollOptional.get()
                    .isUserChoicesAllowed()) {
                var message = event.getMessage().getContentStripped();
                var poll = pollOptional.get();
                var pollChoices = poll.getChoices();
                if (!pollChoices.contains(message) && pollChoices.size() < Constants.POLL_CHOICE_LIMIT) {
                    poll.getChoices().add(message);
                    pollRepository.save(poll);
                    refMessage.editMessage(poll.build()).queue();
                    pollAnnouncementSetup.setupAnnounce(poll, event.getJDA().getShardManager(), refMessage);
                } else {
                    event.getMessage().addReaction(Emojis.CROSS_X).queue();
                }
            }
        }
    }
}
