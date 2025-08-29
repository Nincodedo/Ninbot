package dev.nincodedo.ninbot.components.stream;

import com.github.twitch4j.TwitchClient;
import dev.nincodedo.nincord.command.component.ButtonInteraction;
import dev.nincodedo.nincord.command.component.ComponentData;
import dev.nincodedo.nincord.message.ButtonInteractionCommandMessageExecutor;
import dev.nincodedo.nincord.message.MessageExecutor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StreamButtonInteraction implements ButtonInteraction {

    private StreamingMemberRepository streamingMemberRepository;
    private TwitchClient twitchClient;

    public StreamButtonInteraction(StreamingMemberRepository streamingMemberRepository, TwitchClient twitchClient) {
        this.streamingMemberRepository = streamingMemberRepository;
        this.twitchClient = twitchClient;
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
                messageExecutor.editEphemeralMessage(resource("button.stream.toggle").formatted(onOff))
                        .clearComponents();
            }
            case TWITCHNAME ->
                    messageExecutor.addModal(updateTwitchName(event.getUser().getId(), event.getGuild().getId()));
            default -> throw new IllegalStateException("Unexpected value: " + buttonAction);
        }
        return messageExecutor;
    }

    private Modal updateTwitchName(String userId, String guildId) {
        var streamingMemberOptional = streamingMemberRepository.findByUserIdAndGuildId(userId, guildId);
        StreamingMember streamingMember = streamingMemberOptional.orElseGet(() -> new StreamingMember(userId, guildId));
        if (streamingMemberOptional.isEmpty()) {
            streamingMemberRepository.save(streamingMember);
        }
        return Modal.create("stream-twitchname-" + userId, "Update Twitch Username")
                .addComponents(ActionRow.of(TextInput.create("stream-twitchname", "What's your Twitch username?",
                                TextInputStyle.SHORT)
                        .setMinLength(4)
                        .setMaxLength(25)
                        .setValue(streamingMember.getTwitchUsername())
                        .build()))
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
        if (Boolean.TRUE.equals(streamingMember.getAnnounceEnabled()) && streamingMember.getTwitchUsername() != null) {
            twitchClient.getClientHelper().enableStreamEventListener(streamingMember.getTwitchUsername());
        } else if (Boolean.FALSE.equals(streamingMember.getAnnounceEnabled())
                && streamingMember.getTwitchUsername() != null) {
            twitchClient.getClientHelper().disableStreamEventListener(streamingMember.getTwitchUsername());
        }
        streamingMemberRepository.save(streamingMember);
        return streamingMember.getAnnounceEnabled();
    }

    @Override
    public String getName() {
        return StreamCommandName.STREAM.get();
    }
}
