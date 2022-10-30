package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.command.component.ButtonInteraction;
import dev.nincodedo.ninbot.common.command.component.ComponentData;
import dev.nincodedo.ninbot.common.message.ButtonInteractionCommandMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StreamButtonInteraction implements ButtonInteraction {

    private StreamingMemberRepository streamingMemberRepository;

    public StreamButtonInteraction(StreamingMemberRepository streamingMemberRepository) {
        this.streamingMemberRepository = streamingMemberRepository;
    }

    @Override
    public MessageExecutor executeButtonPress(
            @NotNull ButtonInteractionEvent event, ComponentData componentData) {
        var messageExecutor = new ButtonInteractionCommandMessageExecutor(event);
        var buttonAction = StreamCommandName.Button.valueOf(componentData.action().toUpperCase());
        if (buttonAction == StreamCommandName.Button.NOTHING) {
            messageExecutor.editEphemeralMessage(resource("button.stream.nothing"))
                    .clearComponents();
        } else if (buttonAction == StreamCommandName.Button.TOGGLE) {
            var found = toggleConfig(event.getUser().getId(), event.getGuild().getId());
            var onOff = found ? "on" : "off";
            messageExecutor.editEphemeralMessage(resource("button.stream.toggle." + onOff))
                    .clearComponents();
        }
        return messageExecutor;
    }

    @Override
    public Logger log() {
        return log;
    }

    private boolean toggleConfig(String userId, String serverId) {
        var streamingMemberOptional = streamingMemberRepository.findByUserIdAndGuildId(userId, serverId);
        StreamingMember streamingMember;
        if (streamingMemberOptional.isPresent()) {
            streamingMember = streamingMemberOptional.get();
            streamingMember.setAnnounceEnabled(!streamingMember.getAnnounceEnabled());
        } else {
            streamingMember = new StreamingMember(userId, serverId);
            streamingMember.setAnnounceEnabled(true);
        }
        streamingMemberRepository.save(streamingMember);
        return streamingMember.getAnnounceEnabled();
    }

    @Override
    public String getName() {
        return StreamCommandName.STREAM.get();
    }
}
