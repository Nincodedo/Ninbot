package dev.nincodedo.ninbot.components.channel.thread;

import dev.nincodedo.nincord.BaseListenerAdapter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchiveTimestampEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Component
public class ThreadUnarchivedAnnouncement extends BaseListenerAdapter {

    @Getter
    private final List<String> threadChannelIdList = new ArrayList<>();
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onChannelUpdateArchived(@NotNull ChannelUpdateArchivedEvent event) {
        var threadChannel = event.getChannel().asThreadChannel();
        if (!threadChannel.isPublic()) {
            return;
        }
        if (Boolean.TRUE.equals(event.getOldValue()) && Boolean.FALSE.equals(event.getNewValue())) {
            threadChannel.retrieveMessageById(threadChannel.getLatestMessageId()).queue(checkMessage(threadChannel));
        }
    }

    Consumer<Message> checkMessage(ThreadChannel threadChannel) {
        return message -> {
            var amount = 10;
            var unit = ChronoUnit.SECONDS;
            if (message.getTimeCreated().isAfter(OffsetDateTime.now().minus(amount, unit))) {
                var threadId = threadChannel.getId();
                log.trace("Thread {} unarchived with a recent message", threadId);
                determineThreadAnnouncement(threadChannel, threadId);
            }
        };
    }

    @Override
    public void onChannelUpdateArchiveTimestamp(@NotNull ChannelUpdateArchiveTimestampEvent event) {
        var threadChannel = event.getChannel().asThreadChannel();
        if (!threadChannel.isPublic()) {
            return;
        }
        var minutesHighThreshold = threadChannel.getAutoArchiveDuration().getMinutes() + 1;
        if (event.getOldValue() != null && event.getOldValue()
                .isBefore(OffsetDateTime.now().minus(minutesHighThreshold, ChronoUnit.MINUTES))) {
            var threadId = threadChannel.getId();
            log.trace("Thread {} archive timestamp updated from {} to {}", threadId, event.getOldValue(),
                    event.getNewValue());
            determineThreadAnnouncement(threadChannel, threadId);
        }
    }

    private void determineThreadAnnouncement(ThreadChannel threadChannel, String threadId) {
        boolean isDelayedRemove = false;
        synchronized (threadChannelIdList) {
            if (threadChannelIdList.contains(threadId)) {
                log.trace("Thread {} meets both criteria, announcing revival", threadId);
                announceThreadRevival(threadChannel);
                threadChannelIdList.remove(threadId);
            } else {
                log.trace("Thread {} meets one criteria, adding to monitoring list", threadId);
                threadChannelIdList.add(threadId);
                isDelayedRemove = true;
            }
        }
        if (isDelayedRemove) {
            executorService.schedule(() -> threadChannelIdList.remove(threadId), 10, TimeUnit.SECONDS);
        }
    }

    private void announceThreadRevival(ThreadChannel threadChannel) {
        var parentChannel = threadChannel.getParentMessageChannel();
        var threadChannelMention = threadChannel.getAsMention();
        parentChannel.sendMessageFormat(resource("threadunarchiveannouncement.threadrevived"), threadChannelMention)
                .queue();
    }
}
