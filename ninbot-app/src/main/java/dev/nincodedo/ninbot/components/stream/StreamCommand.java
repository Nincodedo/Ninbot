package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.nincord.command.slash.SlashCommand;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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
            @NotNull SlashCommandInteractionEvent event, @NotNull SlashCommandEventMessageExecutor messageExecutor) {
        var userId = event.getUser().getId();
        var streamingMember = streamingMemberRepository.findByUserIdAndGuildId(userId, event.getGuild()
                .getId()).orElse(new StreamingMember());
        boolean announcementsEnabled = streamingMember.getAnnounceEnabled();
        var resourceBundle = resourceBundle(event.getUserLocale());
        var onOff = announcementsEnabled ? resourceBundle.getString("common.on") : resourceBundle.getString("common"
                + ".off");
        var opposite = announcementsEnabled ? resourceBundle.getString("common.off") : resourceBundle.getString(
                "common.on");
        var createBuilder = new MessageCreateBuilder();
        messageExecutor.addEphemeralMessage(createBuilder.addContent(resourceBundle.getString(
                        "command.stream.announcements").formatted(onOff, opposite))
                .addComponents(ActionRow.of(getPrimaryButton(userId, opposite, resourceBundle),
                        getSecondaryButton(userId, onOff, resourceBundle)))
                .addContent(STR."\n\{resourceBundle.getString("command.stream.twitch.username")}\{streamingMember.getTwitchUsername()}")
                .addComponents(ActionRow.of(Button.secondary(
                        STR."stream-twitchname-\{userId}", resourceBundle.getString("command.stream.button.update"))))
                .build());
        return messageExecutor;
    }

    @NotNull
    private Button getSecondaryButton(String userId, String onOff, ResourceBundle resourceBundle) {
        return Button.secondary(
                STR."stream-nothing-\{userId}", resourceBundle.getString("command.stream.button.secondary")
                        .formatted(onOff));
    }

    @NotNull
    private Button getPrimaryButton(String userId, String opposite, ResourceBundle resourceBundle) {
        return Button.primary(
                STR."stream-toggle-\{userId}", resourceBundle.getString("command.stream.button.primary")
                        .formatted(opposite));
    }
}
