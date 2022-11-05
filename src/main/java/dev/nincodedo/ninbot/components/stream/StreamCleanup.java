package dev.nincodedo.ninbot.components.stream;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class StreamCleanup {

    private StreamingMemberRepository streamingMemberRepository;
    private ShardManager shardManager;

    public StreamCleanup(StreamingMemberRepository streamingMemberRepository, ShardManager shardManager) {
        this.streamingMemberRepository = streamingMemberRepository;
        this.shardManager = shardManager;
    }

    @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
    protected void endOldStreams() {
        log.trace("Running scheduled end of old streams");
        streamingMemberRepository.findAll().forEach(streamingMember -> {
            if (streamingMember.currentStream().isPresent() && streamingMember.currentStream().get().isStreaming()) {
                endOldStreams(streamingMember);
            }
        });
    }

    protected void endOldStreams(StreamingMember streamingMember) {
        streamingMember.currentStream().ifPresent(streamInstance -> {
            var guild = shardManager.getGuildById(streamingMember.getGuildId());
            if (guild != null) {
                guild.retrieveMemberById(streamingMember.getUserId()).queue(member -> {
                    if (member.getActivities().isEmpty()) {
                        //if member has no current activities then end any stream instances that haven't been ended yet
                        streamInstance.getStreamingMember().getStreamInstances().forEach(streamInstance1 -> {
                            if (streamInstance1.getEndTimestamp() == null) {
                                streamInstance1.setEndTimestamp(LocalDateTime.now());
                            }
                        });
                    }
                    streamingMemberRepository.save(streamingMember);
                });
            }
        });
    }

    @Scheduled(fixedDelay = 7, timeUnit = TimeUnit.DAYS)
    protected void removeOldStreams() {
        log.trace("Removing old stream instances");
        var oldDate = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        streamingMemberRepository.findAll().forEach(streamingMember -> {
            var list = streamingMember.getStreamInstances()
                    .stream()
                    .filter(streamInstance -> streamInstance.getEndTimestamp() != null)
                    .filter(streamInstance -> streamInstance.getEndTimestamp().isBefore(oldDate))
                    .toList();
            if (!list.isEmpty()) {
                log.trace("Removing {} streams from {}", list.size(), streamingMember.getId());
                streamingMember.getStreamInstances().removeAll(list);
                streamingMemberRepository.save(streamingMember);
            }
        });
    }
}
