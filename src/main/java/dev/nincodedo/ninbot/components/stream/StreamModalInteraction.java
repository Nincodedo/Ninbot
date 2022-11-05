package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.command.component.ComponentData;
import dev.nincodedo.ninbot.common.command.component.ModalInteraction;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.ModalInteractionCommandMessageExecutor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StreamModalInteraction implements ModalInteraction {

    private StreamingMemberRepository streamingMemberRepository;

    public StreamModalInteraction(StreamingMemberRepository streamingMemberRepository) {
        this.streamingMemberRepository = streamingMemberRepository;
    }

    @Override
    public String getName() {
        return StreamCommandName.STREAM.get();
    }

    @Override
    public MessageExecutor execute(@NotNull ModalInteractionEvent event, @NotNull ComponentData componentData) {
        var messageExecutor = new ModalInteractionCommandMessageExecutor(event);
        var textInput = event.getValue("stream-twitchname");
        if (textInput != null) {
            var userId = event.getUser().getId();
            var serverId = event.getGuild().getId();
            var twitchUsername = textInput.getAsString();
            var streamingMember = streamingMemberRepository.findByUserIdAndGuildId(userId, serverId)
                    .orElse(new StreamingMember(userId, serverId));
            streamingMember.setTwitchUsername(twitchUsername);
            streamingMemberRepository.save(streamingMember);
            messageExecutor.addEphemeralMessage("Updated your Twitch username to " + twitchUsername);
        }
        return messageExecutor;
    }

    @Override
    public Logger log() {
        return log;
    }
}
