package dev.nincodedo.ninbot.components.stream;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Log4j2
public class StreamCleanup {

    private StreamingMemberRepository streamingMemberRepository;
    private ShardManager shardManager;

    public StreamCleanup(StreamingMemberRepository streamingMemberRepository, ShardManager shardManager) {
        this.streamingMemberRepository = streamingMemberRepository;
        this.shardManager = shardManager;
    }

    //twice a day
    @Scheduled(fixedRate = 43200000L)
    private void endOldStreams() {
        log.trace("Running scheduled end of old streams");
        streamingMemberRepository.findAll().forEach(streamingMember -> {
            if (streamingMember.currentStream().isPresent() && streamingMember.currentStream().get().isStreaming()) {
                endOldStreams(streamingMember);
            }
        });
    }

    private void endOldStreams(StreamingMember streamingMember) {
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
}
