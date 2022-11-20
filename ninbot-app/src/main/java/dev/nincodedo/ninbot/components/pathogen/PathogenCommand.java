package dev.nincodedo.ninbot.components.pathogen;

import dev.nincodedo.ninbot.components.pathogen.user.PathogenUserRepository;
import dev.nincodedo.nincord.Emojis;
import dev.nincodedo.nincord.command.slash.SlashCommand;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class PathogenCommand implements SlashCommand {

    private PathogenUserRepository pathogenUserRepository;

    public PathogenCommand(PathogenUserRepository pathogenUserRepository) {
        this.pathogenUserRepository = pathogenUserRepository;
    }

    @Override
    public MessageExecutor execute(@NotNull SlashCommandInteractionEvent event,
            @NotNull SlashCommandEventMessageExecutor messageExecutor) {
        messageExecutor.addMessageResponse(getUserInfectionLevel(event));
        return messageExecutor;
    }

    private MessageCreateData getUserInfectionLevel(SlashCommandInteractionEvent event) {
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        var pathogenUser = pathogenUserRepository.getByUserIdAndServerId(event.getUser()
                .getId(), event.getGuild().getId());
        if (pathogenUser != null) {
            if (Boolean.TRUE.equals(pathogenUser.getVaccinated())) {
                messageBuilder.addContent(Emojis.getRandomDoctorEmoji());
                messageBuilder.addContent(Emojis.getNumberMap().get(0));
                messageBuilder.addContent(Emojis.THUMBS_UP);
            } else if (pathogenUser.getInfectionLevel() == 0) {
                messageBuilder.addContent(Emojis.HAPPY_FACE);
                messageBuilder.addContent(Emojis.getNumberMap().get(0));
            } else if (pathogenUser.getInfectionLevel() > 0) {
                messageBuilder.addContent(Emojis.SICK_FACE);
                messageBuilder.addContent(Emojis.getNumberMap().get(pathogenUser.getInfectionLevel()));
            }
        }
        return messageBuilder.build();
    }

    @Override
    public String getName() {
        return PathogenCommandName.PATHOGEN.get();
    }
}
