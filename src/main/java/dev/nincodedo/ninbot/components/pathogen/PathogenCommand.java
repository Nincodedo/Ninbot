package dev.nincodedo.ninbot.components.pathogen;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import dev.nincodedo.ninbot.components.pathogen.user.PathogenUserRepository;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class PathogenCommand implements SlashCommand {

    private PathogenUserRepository pathogenUserRepository;

    public PathogenCommand(PathogenUserRepository pathogenUserRepository) {
        this.pathogenUserRepository = pathogenUserRepository;
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        messageExecutor.addMessageResponse(getUserInfectionLevel(slashCommandEvent));
        return messageExecutor;
    }

    private Message getUserInfectionLevel(SlashCommandInteractionEvent event) {
        MessageBuilder messageBuilder = new MessageBuilder();
        var pathogenUser = pathogenUserRepository.getByUserIdAndServerId(event.getUser()
                .getId(), event.getGuild().getId());
        if (pathogenUser != null) {
            if (Boolean.TRUE.equals(pathogenUser.getVaccinated())) {
                messageBuilder.append(Emojis.getRandomDoctorEmoji());
                messageBuilder.append(Emojis.getNumberMap().get(0));
                messageBuilder.append(Emojis.THUMBS_UP);
            } else if (pathogenUser.getInfectionLevel() == 0) {
                messageBuilder.append(Emojis.HAPPY_FACE);
                messageBuilder.append(Emojis.getNumberMap().get(0));
            } else if (pathogenUser.getInfectionLevel() > 0) {
                messageBuilder.append(Emojis.SICK_FACE);
                messageBuilder.append(Emojis.getNumberMap().get(pathogenUser.getInfectionLevel()));
            }
        }
        return messageBuilder.build();
    }

    @Override
    public String getName() {
        return PathogenCommandName.PATHOGEN.get();
    }
}
