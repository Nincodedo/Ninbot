package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.val;
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
        val refMessage = event.getMessage().getReferencedMessage();
        if (refMessage != null && refMessage.getId().equals(pollMessageId)) {
            val pollOptional = pollRepository.findByMessageIdAndPollOpen(pollMessageId, true);
            if (pollOptional.isPresent() && pollOptional.get().isUserChoicesAllowed()) {
                val message = event.getMessage().getContentStripped();
                val poll = pollOptional.get();
                poll.getChoices().add(message);
                pollRepository.save(poll);
                refMessage.editMessage(poll.build()).queue();
                pollSetup.setupAnnounce(poll, event.getJDA().getShardManager(), refMessage);
            }
        }
    }


}
