package dev.nincodedo.ninbot.components.reaction.emojitizer;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.ninbot.common.command.component.ComponentData;
import dev.nincodedo.ninbot.common.message.ModalInteractionCommandMessageExecutor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties"})
class EmojitizerModalInteractionTest {

    @InjectMocks
    EmojitizerModalInteraction emojitizerModalInteraction;

    @Captor
    private ArgumentCaptor<Consumer> lambdaCaptor;

    @Test
    void somethingEmojitizable() {
        var componentData = new ComponentData("", "", "1");
        var modalEvent = Mockito.mock(ModalInteractionEvent.class);
        var modalMapping = Mockito.mock(ModalMapping.class);
        var replyCallback = Mockito.mock(ReplyCallbackAction.class);
        var channel = Mockito.mock(GuildMessageChannelUnion.class);
        var messageRetrieveAction = Mockito.mock(MessageHistory.MessageRetrieveAction.class);
        var messageHistory = Mockito.mock(MessageHistory.class);
        var message = Mockito.mock(Message.class);
        var hook = Mockito.mock(InteractionHook.class);
        var webhookEditAction = Mockito.mock(WebhookMessageEditAction.class);
        when(modalEvent.getValue("emojitizer-text")).thenReturn(modalMapping);
        when(modalMapping.getAsString()).thenReturn("big");
        when(modalEvent.deferReply(true)).thenReturn(replyCallback);
        when(modalEvent.getGuildChannel()).thenReturn(channel);
        when(channel.getHistoryAround("1", 1)).thenReturn(messageRetrieveAction);
        when(messageHistory.getMessageById("1")).thenReturn(message);
        when(modalEvent.getHook()).thenReturn(hook);
        when(hook.editOriginal(anyString())).thenReturn(webhookEditAction);

        var messageExecutor = (ModalInteractionCommandMessageExecutor) emojitizerModalInteraction.execute(modalEvent,
                componentData);

        assertThat(messageExecutor.getEphemeralMessageResponses()).isEmpty();

        verify(messageRetrieveAction).queue(lambdaCaptor.capture());

        Consumer<MessageHistory> consumer = lambdaCaptor.getValue();
        consumer.accept(messageHistory);

        verify(webhookEditAction).queue();
    }

    @Test
    void somethingNotEmojitizable() {
        var componentData = new ComponentData("", "", "1");
        var modalEvent = Mockito.mock(ModalInteractionEvent.class);
        var modalMapping = Mockito.mock(ModalMapping.class);
        when(modalEvent.getValue("emojitizer-text")).thenReturn(modalMapping);
        when(modalMapping.getAsString()).thenReturn("aaa");

        var messageExecutor = (ModalInteractionCommandMessageExecutor) emojitizerModalInteraction.execute(modalEvent,
                componentData);

        assertThat(messageExecutor.getEphemeralMessageResponses()).isNotEmpty();
    }

    @Test
    void nullMapping() {
        var componentData = new ComponentData("", "", "1");
        var modalEvent = Mockito.mock(ModalInteractionEvent.class);
        when(modalEvent.getValue("emojitizer-text")).thenReturn(null);

        var messageExecutor = (ModalInteractionCommandMessageExecutor) emojitizerModalInteraction.execute(modalEvent,
                componentData);

        assertThat(messageExecutor.getEphemeralMessageResponses()).isEmpty();
    }
}
