package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.nincord.command.component.ButtonInteraction;
import dev.nincodedo.nincord.command.component.ComponentData;
import dev.nincodedo.nincord.message.ButtonInteractionCommandMessageExecutor;
import dev.nincodedo.nincord.message.MessageExecutor;
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
    public MessageExecutor execute(@NotNull ButtonInteractionEvent event,
            @NotNull ButtonInteractionCommandMessageExecutor messageExecutor, @NotNull ComponentData componentData) {
        if (event.getGuild() == null) {
            return messageExecutor;
        }
        var buttonAction = StreamCommandName.Button.valueOf(componentData.action().toUpperCase());
        switch (buttonAction) {
            case NOTHING -> messageExecutor.editEphemeralMessage(resource("button.stream.nothing")).clearComponents();
            case TOGGLE -> {
                var found = toggleConfig(event.getUser().getId(), event.getGuild().getId());
                var onOff = found ? resource("common.on") : resource("common.off");
                messageExecutor.editEphemeralMessage(String.format(resource("button.stream.toggle"), onOff))
                        .clearComponents();
            }
            case TWITCHNAME ->
                    messageExecutor.addModal(updateTwitchName(event.getUser().getId(), event.getGuild().getId()));
            default -> throw new IllegalStateException("Unexpected value: " + buttonAction);
        }
        return messageExecutor;
    }

    private Modal updateTwitchName(String userId, String guildId) {
        var streamingMember = streamingMemberRepository.findByUserIdAndGuildId(userId, guildId)
                .orElse(new StreamingMember());
        return Modal.create("stream-twitchname-" + userId, "Update Twitch Username")
                .addActionRow(TextInput.create("stream-twitchname", "What's your Twitch username?",
                                TextInputStyle.SHORT)
                        .setMinLength(4)
                        .setMaxLength(25)
                        .setValue(streamingMember.getTwitchUsername())
                        .build())
                .build();
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