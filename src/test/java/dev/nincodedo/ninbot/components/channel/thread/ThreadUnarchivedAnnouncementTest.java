package dev.nincodedo.ninbot.components.channel.thread;

import dev.nincodedo.ninbot.NinbotRunner;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.ChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchiveTimestampEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.OffsetDateTime;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class ThreadUnarchivedAnnouncementTest {

    @InjectMocks
    ThreadUnarchivedAnnouncement threadUnarchivedAnnouncement;
    @Captor
    private ArgumentCaptor<Consumer> lambdaCaptor;

    @Test
    void threadUnarchivedWithAnnouncement() {
        ChannelUpdateArchivedEvent updateArchivedEvent = Mockito.mock(ChannelUpdateArchivedEvent.class);
        ChannelUnion channelUnion = Mockito.mock(ChannelUnion.class);
        ThreadChannel threadChannel = Mockito.mock(ThreadChannel.class);
        RestAction restAction = Mockito.mock(RestAction.class);
        Message message = Mockito.mock(Message.class);
        when(updateArchivedEvent.getOldValue()).thenReturn(Boolean.TRUE);
        when(updateArchivedEvent.getNewValue()).thenReturn(Boolean.FALSE);
        when(updateArchivedEvent.getChannel()).thenReturn(channelUnion);
        when(channelUnion.asThreadChannel()).thenReturn(threadChannel);
        when(threadChannel.isPublic()).thenReturn(true);
        when(threadChannel.getLatestMessageId()).thenReturn("222");
        when(threadChannel.retrieveMessageById(anyString())).thenReturn(restAction);
        when(message.getTimeCreated()).thenReturn(OffsetDateTime.now().minusSeconds(1));
        when(threadChannel.getId()).thenReturn("1");

        assertThat(threadUnarchivedAnnouncement.getThreadChannelIdList()).isEmpty();
        threadUnarchivedAnnouncement.onChannelUpdateArchived(updateArchivedEvent);

        verify(restAction).queue(lambdaCaptor.capture());

        Consumer<Message> messageConsumer = lambdaCaptor.getValue();
        messageConsumer.accept(message);

        assertThat(threadUnarchivedAnnouncement.getThreadChannelIdList()).isNotEmpty();

        ChannelUpdateArchiveTimestampEvent updateArchiveTimestampEvent =
                Mockito.mock(ChannelUpdateArchiveTimestampEvent.class);
        ThreadChannel.AutoArchiveDuration autoArchiveDuration = ThreadChannel.AutoArchiveDuration.TIME_1_HOUR;
        GuildMessageChannelUnion guildMessageChannel = Mockito.mock(GuildMessageChannelUnion.class);
        MessageCreateAction messageAction = Mockito.mock(MessageCreateAction.class);

        when(updateArchiveTimestampEvent.getChannel()).thenReturn(channelUnion);
        when(threadChannel.getAutoArchiveDuration()).thenReturn(autoArchiveDuration);
        when(updateArchiveTimestampEvent.getOldValue()).thenReturn(OffsetDateTime.now().minusHours(2));
        when(threadChannel.getParentMessageChannel()).thenReturn(guildMessageChannel);
        when(threadChannel.getAsMention()).thenReturn("#blerg");
        when(guildMessageChannel.sendMessageFormat(anyString(), anyString())).thenReturn(messageAction);

        threadUnarchivedAnnouncement.onChannelUpdateArchiveTimestamp(updateArchiveTimestampEvent);

        assertThat(threadUnarchivedAnnouncement.getThreadChannelIdList()).isEmpty();
        verify(messageAction).queue();
    }

    @Test
    void privateThreadUpdateArchivedUnarchivedWithoutAnnouncement() {
        ChannelUpdateArchivedEvent updateArchivedEvent = Mockito.mock(ChannelUpdateArchivedEvent.class);
        ChannelUnion channelUnion = Mockito.mock(ChannelUnion.class);
        ThreadChannel channel = Mockito.mock(ThreadChannel.class);
        when(updateArchivedEvent.getChannel()).thenReturn(channelUnion);
        when(channelUnion.asThreadChannel()).thenReturn(channel);
        when(channel.isPublic()).thenReturn(false);
        threadUnarchivedAnnouncement.onChannelUpdateArchived(updateArchivedEvent);
        verifyNoMoreInteractions(updateArchivedEvent, channel);
    }

    @Test
    void privateThreadUpdateTimestampUnarchivedWithoutAnnouncement() {
        ChannelUpdateArchiveTimestampEvent updateArchiveTimestampEvent =
                Mockito.mock(ChannelUpdateArchiveTimestampEvent.class);
        ChannelUnion channelUnion = Mockito.mock(ChannelUnion.class);
        ThreadChannel channel = Mockito.mock(ThreadChannel.class);
        when(updateArchiveTimestampEvent.getChannel()).thenReturn(channelUnion);
        when(channelUnion.asThreadChannel()).thenReturn(channel);
        when(channel.isPublic()).thenReturn(false);
        threadUnarchivedAnnouncement.onChannelUpdateArchiveTimestamp(updateArchiveTimestampEvent);
        verifyNoMoreInteractions(updateArchiveTimestampEvent, channel);
    }
}
