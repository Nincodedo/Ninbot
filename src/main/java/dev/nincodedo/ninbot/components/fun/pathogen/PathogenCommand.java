package dev.nincodedo.ninbot.components.fun.pathogen;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import dev.nincodedo.ninbot.components.fun.pathogen.user.PathogenUserRepository;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.stereotype.Component;

@Component
public class PathogenCommand implements SlashCommand {

    private PathogenUserRepository pathogenUserRepository;

    public PathogenCommand(PathogenUserRepository pathogenUserRepository) {
        this.pathogenUserRepository = pathogenUserRepository;
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        slashCommandEvent.reply(getUserInfectionLevel(slashCommandEvent)).queue();
    }

    private Message getUserInfectionLevel(SlashCommandEvent event) {
        MessageBuilder messageBuilder = new MessageBuilder();
        var pathogenUser = pathogenUserRepository.getByUserIdAndServerId(event.getUser()
                .getId(), event.getGuild().getId());
        if (pathogenUser != null) {
            if (pathogenUser.getVaccinated()) {
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
        return "pathogen";
    }
}
