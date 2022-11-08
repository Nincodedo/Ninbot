package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.command.component.ButtonInteraction;
import dev.nincodedo.ninbot.common.command.component.ComponentData;
import dev.nincodedo.ninbot.common.message.ButtonInteractionCommandMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
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
            var onOff = found ? resource("common.on") : resource("common.off");
            messageExecutor.editEphemeralMessage(String.format(resource("button.stream.toggle"), onOff))
                    .clearComponents();
        } else if (buttonAction == StreamCommandName.Button.TWITCHNAME) {
            var userId = event.getUser().getId();
            var serverId = event.getGuild().getId();
            var streamingMember = streamingMemberRepository.findByUserIdAndGuildId(userId, serverId)
                    .orElse(new StreamingMember());
            Modal modal = Modal.create("stream-twitchname-" + userId, "Update Twitch Username")
                    .addActionRow(TextInput.create(
                                    "stream-twitchname", "What's your Twitch username?", TextInputStyle.SHORT)
                            .setMinLength(4)
                            .setMaxLength(25)
                            .setValue(streamingMember.getTwitchUsername())
                            .build())
                    .build();
            event.replyModal(modal).queue();
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
