package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class StreamCommand implements SlashCommand {

    private StreamingMemberRepository streamingMemberRepository;

    public StreamCommand(StreamingMemberRepository streamingMemberRepository) {
        this.streamingMemberRepository = streamingMemberRepository;
    }

    @Override
    public String getName() {
        return StreamCommandName.STREAM.get();
    }

    @Override
    public MessageExecutor execute(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        var userId = slashCommandEvent.getUser().getId();
        var announcementsEnabled = isAnnouncementsEnabledForUser(userId, slashCommandEvent.getGuild().getId());
        var onOff = announcementsEnabled ? "on" : "off";
        var opposite = announcementsEnabled ? "off" : "on";
        var createBuilder = new MessageCreateBuilder();
        messageExecutor.addEphemeralMessage(createBuilder.addContent(
                        "Stream announcements are currently " + onOff +
                                ". Would you like to turn them " + opposite + "?")
                .addComponents(ActionRow.of(getPrimaryButton(userId, opposite), getSecondaryButton(userId, onOff)))
                .build());
        return messageExecutor;
    }

    private boolean isAnnouncementsEnabledForUser(String userId, String guildId) {
        var streamMember = streamingMemberRepository.findByUserIdAndGuildId(userId, guildId);
        if (streamMember.isPresent()) {
            return streamMember.get().getAnnounceEnabled();
        } else {
            return false;
        }
    }

    @NotNull
    private Button getSecondaryButton(String userId, String onOff) {
        return Button.secondary("stream-nothing-" + userId, "No, keep them " + onOff);
    }

    @NotNull
    private Button getPrimaryButton(String userId, String opposite) {
        return Button.primary("stream-toggle-" + userId, "Yes, turn them " + opposite);
    }
}
