package dev.nincodedo.ninbot.components.eightball;

import dev.nincodedo.nincord.command.component.ButtonInteraction;
import dev.nincodedo.nincord.command.component.ComponentData;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.message.ButtonInteractionCommandMessageExecutor;
import dev.nincodedo.nincord.message.MessageExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Magic8BallReshakeButtonInteraction implements ButtonInteraction {

    private final Magic8BallMessageBuilder magic8BallMessageBuilder;
    private final ConfigService configService;

    @Override
    public MessageExecutor execute(@NotNull ButtonInteractionEvent event,
            @NotNull ButtonInteractionCommandMessageExecutor messageExecutor, @NotNull ComponentData componentData) {
        event.deferEdit().queue();
        var expectedUserId = componentData.data();
        if (!expectedUserId.equals(event.getUser().getId())) {
            return messageExecutor;
        }
        var message = event.getMessage();
        var serverId = event.getGuild() != null ? event.getGuild().getId() : "0";
        var maxShakesConfigOptional = configService.getGlobalConfigByName(ConfigConstants.MAGIC_EIGHT_BALL_MAX_SHAKES
                , serverId);
        int maxShakes = maxShakesConfigOptional.map(config -> Integer.parseInt(config.getValue())).orElse(8);
        var editMessage = magic8BallMessageBuilder.reshake(message, maxShakes);
        event.getHook().editOriginal(editMessage.build()).queue();
        return messageExecutor;
    }

    @Override
    public Logger log() {
        return log;
    }

    @Override
    public String getName() {
        return Magic8BallCommandName.EIGHTBALL.get();
    }
}
