package dev.nincodedo.ninbot.components.haiku;

import dev.nincodedo.nincord.command.component.ButtonInteraction;
import dev.nincodedo.nincord.command.component.ComponentData;
import dev.nincodedo.nincord.message.ButtonInteractionCommandMessageExecutor;
import dev.nincodedo.nincord.message.MessageExecutor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HaikuAnalyzeShareButtonInteraction implements ButtonInteraction {
    @Override
    public MessageExecutor execute(@NotNull ButtonInteractionEvent event,
            @NotNull ButtonInteractionCommandMessageExecutor messageExecutor, @NotNull ComponentData componentData) {
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder.setEmbeds(event.getMessage().getEmbeds());
        messageExecutor.addMessageResponse(messageCreateBuilder.build());
        return messageExecutor;
    }

    @Override
    public Logger log() {
        return log;
    }

    @Override
    public String getName() {
        return "haiku";
    }
}
