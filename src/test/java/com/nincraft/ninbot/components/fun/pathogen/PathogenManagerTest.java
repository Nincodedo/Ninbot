package com.nincraft.ninbot.components.fun.pathogen;

import com.nincraft.ninbot.NinbotTest;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class PathogenManagerTest extends NinbotTest {

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