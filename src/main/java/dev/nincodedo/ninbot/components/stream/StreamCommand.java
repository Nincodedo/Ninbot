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

import java.util.ResourceBundle;

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
        var streamingMember = streamingMemberRepository.findByUserIdAndGuildId(userId, slashCommandEvent.getGuild()
                .getId()).orElse(new StreamingMember());
        boolean announcementsEnabled = streamingMember.getAnnounceEnabled();
        var resourceBundle = resourceBundle(slashCommandEvent.getUserLocale());
        var onOff = announcementsEnabled ? resourceBundle.getString("common.on") : resourceBundle.getString("common"
                + ".off");
        var opposite = announcementsEnabled ? resourceBundle.getString("common.off") : resourceBundle.getString(
                "common.on");
        var createBuilder = new MessageCreateBuilder();
        messageExecutor.addEphemeralMessage(createBuilder.addContent(String.format(resourceBundle.getString(
                        "command" + ".stream.announcements"), onOff, opposite))
                .addComponents(ActionRow.of(getPrimaryButton(userId, opposite, resourceBundle),
                        getSecondaryButton(userId, onOff, resourceBundle)))
                .addContent("\n" + resourceBundle.getString("command.stream.twitch.username")
                        + streamingMember.getTwitchUsername())
                .addComponents(ActionRow.of(Button.secondary(
                        "stream-twitchname-" + userId, resourceBundle.getString("command.stream.button.update"))))
                .build());
        return messageExecutor;
    }

    @NotNull
    private Button getSecondaryButton(String userId, String onOff, ResourceBundle resourceBundle) {
        return Button.secondary(
                "stream-nothing-"
                        + userId, String.format(resourceBundle.getString("command.stream.button.secondary"), onOff));
    }

    @NotNull
    private Button getPrimaryButton(String userId, String opposite, ResourceBundle resourceBundle) {
        return Button.primary(
                "stream-toggle-"
                        + userId, String.format(resourceBundle.getString("command.stream.button.primary"), opposite));
    }
}
