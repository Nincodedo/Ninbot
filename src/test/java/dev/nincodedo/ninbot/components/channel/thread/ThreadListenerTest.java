package dev.nincodedo.ninbot.components.channel.thread;

import dev.nincodedo.ninbot.NinbotRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class ThreadListenerTest {

    @Test
    void onChannelUpdateArchived() {
    }

    @Test
    void onChannelUpdateArchiveTimestamp() {
    }
}
