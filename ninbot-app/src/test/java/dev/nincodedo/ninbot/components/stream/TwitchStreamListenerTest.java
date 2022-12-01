package dev.nincodedo.ninbot.components.stream;

import com.github.twitch4j.TwitchClient;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwitchStreamListenerTest {
    @Mock
    StreamAnnouncer streamAnnouncer;
    @Mock
    ComponentService componentService;

    @Mock
    StreamingMemberRepository streamingMemberRepository;

    @Mock(answer = Answers.RETURNS_MOCKS)
    TwitchClient twitchClient;

    @Mock
    ExecutorService executorService;

    @InjectMocks
    TwitchStreamListener twitchStreamListener;

    @Test
    void streamStarts() {
        int listSize = 5;
        List<StreamingMember> streamingMemberList = Instancio.of(StreamingMember.class)
                .set(field("twitchUsername"), "nincodedo")
                .set(field("announceEnabled"), true).stream().limit(listSize).toList();
        when(streamingMemberRepository.findAllByTwitchUsername("nincodedo")).thenReturn(streamingMemberList);
        twitchStreamListener.streamStarts("nincodedo", "Kirby 64", "what a surprise");
        verify(streamingMemberRepository, times(listSize)).save(any());
        verify(streamAnnouncer, times(listSize)).announceStream(any(), eq("Kirby 64"), eq("what a surprise"));
    }
}
