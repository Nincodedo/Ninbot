package dev.nincodedo.ninbot.components.tally;

import dev.nincodedo.nincord.message.EphemeralMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TallyCommandTest {

    TallyCommand tallyCommand = new TallyCommand();

    @BeforeEach
    void before() {
        TallyCommand.getTallyCount().clear();
    }

    @Test
    void addTally() {
        SlashCommandInteractionEvent event = Mockito.mock(SlashCommandInteractionEvent.class);
        OptionMapping optionMapping = Mockito.mock(OptionMapping.class);

        when(event.getSubcommandName()).thenReturn("add");
        when(event.getOption("count")).thenReturn(optionMapping);
        when(optionMapping.getAsLong()).thenReturn(3L);
        when(event.getOption(eq("name"), any())).thenReturn("test");

        tallyCommand.execute(event);

        var tallyMap = TallyCommand.getTallyCount();
        assertThat(tallyMap).containsEntry("test", 3);
    }

    @Test
    void getTally() {
        SlashCommandInteractionEvent event = Mockito.mock(SlashCommandInteractionEvent.class);

        when(event.getSubcommandName()).thenReturn("get");
        when(event.getOption(eq("name"), any())).thenReturn("test");

        var messageExecutor = (EphemeralMessageExecutor)tallyCommand.execute(event);

        assertThat(messageExecutor.getEphemeralMessageResponses().get(0).getContent()).contains("No count");

        SlashCommandInteractionEvent event2 = Mockito.mock(SlashCommandInteractionEvent.class);
        OptionMapping optionMapping = Mockito.mock(OptionMapping.class);

        when(event2.getSubcommandName()).thenReturn("add");
        when(event2.getOption("count")).thenReturn(optionMapping);
        when(optionMapping.getAsLong()).thenReturn(3L);
        when(event2.getOption(eq("name"), any())).thenReturn("test");

        tallyCommand.execute(event2);

        var messageExecutor2 = tallyCommand.execute(event);

        assertThat(messageExecutor2.getMessageResponses().get(0).getContent()).contains("test count: 3");
    }
}
