package dev.nincodedo.ninbot.components.channel.thread;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchiveTimestampEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ThreadListener extends ListenerAdapter {

    private final Map<String, ThreadChannelInfo> threadChannelInfoMap = new HashMap<>();

    @Override
    public void onChannelUpdateArchived(@NotNull ChannelUpdateArchivedEvent event) {
        if (Boolean.TRUE.equals(event.getOldValue()) && Boolean.FALSE.equals(event.getNewValue())) {
            log.trace("A thread has been unarchived.");
            var threadChannel = (ThreadChannel) event.getChannel();
            threadChannel.retrieveMessageById(threadChannel.getLatestMessageId()).queue(message -> {
                var amount = 10;
                var unit = ChronoUnit.SECONDS;
                if (message.getTimeCreated().isAfter(OffsetDateTime.now().minus(amount, unit))) {
                    log.trace(String.format("Thread has a new message within the past %s %s. That message prooobably "
                            + "unarchived it.", amount, unit.name()));
                    var threadId = threadChannel.getId();
                    synchronized (threadChannelInfoMap) {
                        if (threadChannelInfoMap.containsKey(threadId)) {
                            announceThreadRevival(threadChannelInfoMap.get(threadId));
                        } else {
                            ThreadChannelInfo threadChannelInfo = new ThreadChannelInfo(threadId);
                            threadChannelInfoMap.put(threadId, threadChannelInfo);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onChannelUpdateArchiveTimestamp(@NotNull ChannelUpdateArchiveTimestampEvent event) {
        var threadChannel = (ThreadChannel) event.getChannel();
        var minutesHighThreashold = threadChannel.getAutoArchiveDuration().getMinutes() * 3;
        if (event.getOldValue() != null && event.getOldValue()
                .isBefore(OffsetDateTime.now().minus(minutesHighThreashold, ChronoUnit.MINUTES))) {

        }

    }

    private void announceThreadRevival(ThreadChannelInfo threadChannelInfo) {

    }
}


