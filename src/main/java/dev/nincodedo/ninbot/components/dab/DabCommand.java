package dev.nincodedo.ninbot.components.dab;

import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DabCommand implements SlashCommand {

    private Dabber dabber;

    public DabCommand(Dabber dabber) {
        this.dabber = dabber;
    }

    @Override
    public String getName() {
        return DabCommandName.DAB.get();
    }

    @Override
    public MessageExecutor execute(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        doDabarinos(slashCommandEvent.getJDA()
                        .getShardManager(), slashCommandEvent.getMessageChannel(), slashCommandEvent.getUser(),
                messageExecutor,
                slashCommandEvent.getOption(DabCommandName.Option.DABBED.get(), OptionMapping::getAsUser));
        var user = slashCommandEvent.getOption(DabCommandName.Option.DABBED.get(), OptionMapping::getAsUser);
        if (user != null) {
            messageExecutor.addMessageResponse(dabber.buildDabMessage(user));
        }
        return messageExecutor;
    }

    private void doDabarinos(ShardManager shardManager, MessageChannel messageChannel,
            User eventMessageAuthor, MessageExecutor messageExecutor, User dabbedOn) {
        var eventMessageOptional = messageChannel.getIterableHistory()
                .stream()
                .limit(10)
                .filter(message -> message.getAuthor().equals(dabbedOn))
                .findFirst();
        if (eventMessageOptional.isPresent()) {
            messageExecutor.setOverrideMessage(eventMessageOptional.get());
            dabber.dabOnMessage(messageExecutor, shardManager, eventMessageAuthor);
            return;
        }
        messageExecutor.addUnsuccessfulReaction();
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(new OptionData(OptionType.USER, DabCommandName.Option.DABBED.get(), "the poor soul.",
                true));
    }
}
