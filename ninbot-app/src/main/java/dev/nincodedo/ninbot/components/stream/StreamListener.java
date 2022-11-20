package dev.nincodedo.ninbot.components.stream;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface StreamListener {
    default boolean isAnnouncementNotNeeded(StreamingMember streamingMember) {
        var currentStream = streamingMember.currentStream();
        return !streamingMember.getAnnounceEnabled()
                || currentStream.isPresent() && currentStream.get().getAnnounceMessageId() != null;
    }

    default void setupNewStream(StreamingMember streamingMember) {
        var optionalCurrentStream = streamingMember.currentStream();
        //if the streaming member does not have a current stream running, add a new one
        if (optionalCurrentStream.isEmpty()) {
            streamingMember.startNewStream();
        }
        /*if the stream is recent (stream bounced), then keep using this one as the stream has not really
        ended and set the end time to null. if its already null, well its just null again now*/
        else if (isStreamRecent(optionalCurrentStream.get())) {
            var currentStream = optionalCurrentStream.get();
            currentStream.setEndTimestamp(null);
        }
    }

    private boolean isStreamRecent(StreamInstance currentStream) {
        return currentStream.getEndTimestamp() == null || currentStream.getEndTimestamp()
                .isAfter(LocalDateTime.now().minus(5, ChronoUnit.MINUTES));
    }
}
