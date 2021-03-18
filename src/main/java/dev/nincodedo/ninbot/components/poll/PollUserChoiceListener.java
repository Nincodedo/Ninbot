package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.components.common.Constants;
import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PollUserChoiceListener extends StatAwareListenerAdapter {
    private PollRepository pollRepository;
    private PollSetup pollSetup;
    private String pollMessageId;

    public PollUserChoiceListener(StatManager statManager, PollRepository pollRepository, String pollMessageId,
            PollSetup pollSetup) {
        super(statManager);
        this.pollRepository = pollRepository;
        this.pollMessageId = pollMessageId;
        this.pollSetup = pollSetup;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        final net.dv8tion.jda.api.entities.Message refMessage = event.getMessage().getReferencedMessage();
        if (refMessage != null && refMessage.getId().equals(pollMessageId)) {
            final java.util.Optional<dev.nincodedo.ninbot.components.poll.Poll> pollOptional =
                    pollRepository.findByMessageIdAndPollOpen(pollMessageId, true);
            if (pollOptional.isPresent() && pollOptional.get().isPollOpen() && pollOptional.get()
                    .isUserChoicesAllowed()) {
                String message = event.getMessage().getContentStripped();
                final dev.nincodedo.ninbot.components.poll.Poll poll = pollOptional.get();
                final java.util.List<java.lang.String> pollChoices = poll.getChoices();
                if (!pollChoices.contains(message) && pollChoices.size() < Constants.POLL_CHOICE_LIMIT) {
                    poll.getChoices().add(message);
                    pollRepository.save(poll);
                    refMessage.editMessage(poll.build()).queue();
                    pollSetup.setupAnnounce(poll, event.getJDA().getShardManager(), refMessage);
                } else {
                    new MessageAction().setOverrideMessage(event.getMessage())
                            .addUnsuccessfulReaction()
                            .executeActions();
                }
            }
        }
    }
}
