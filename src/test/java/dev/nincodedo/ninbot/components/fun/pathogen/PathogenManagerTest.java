package dev.nincodedo.ninbot.components.fun.pathogen;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.ninbot.components.fun.pathogen.audit.PathogenAuditRepository;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class PathogenManagerTest {

    @InjectMocks
    PathogenManager pathogenManager;

    @Mock
    PathogenAuditRepository pathogenAuditRepository;

    @Test
    void getManyWordLists() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            pathogenManager.setRandomSeed(random.nextLong());
            val wordList = pathogenManager.getWordList();

            assertThat(wordList).hasSize(pathogenManager.getWordListLength());
            assertThat(wordList).doesNotHaveDuplicates();
        }
    }
}