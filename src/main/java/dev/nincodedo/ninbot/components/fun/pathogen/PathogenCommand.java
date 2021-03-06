package dev.nincodedo.ninbot.components.fun.pathogen;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.Emojis;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.fun.pathogen.user.PathogenUser;
import dev.nincodedo.ninbot.components.fun.pathogen.user.PathogenUserRepository;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PathogenCommand extends AbstractCommand {

    private PathogenUserRepository pathogenUserRepository;

    public PathogenCommand(PathogenUserRepository pathogenUserRepository) {
        name = "pathogen";
        aliases = Arrays.asList("infected", "infection");
        checkExactLength = false;
        this.pathogenUserRepository = pathogenUserRepository;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "stats" -> messageAction.addChannelAction(getInfectionServerStats(event.getGuild()));
            default -> messageAction.addReaction(getUserInfectionLevel(event));
        }
        return messageAction;
    }

    private Message getInfectionServerStats(Guild guild) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        val roleList = guild.getRolesByName(PathogenConfig.getINFECTED_ROLE_NAME(), true);
        if (!roleList.isEmpty()) {
            val users = guild.getMembersWithRoles(roleList);
            List<String> userIds = users.stream().map(ISnowflake::getId).collect(Collectors.toList());
            val infectedUsers = pathogenUserRepository.getAllByUserIdIsIn(userIds);
            embedBuilder.addField(resourceBundle.getString("command.pathogen.stats.size"),
                    String.valueOf(infectedUsers.size()), false);
            infectedUsers.stream()
                    .max(Comparator.comparing(PathogenUser::getInfectionLevel))
                    .ifPresent(pathogenUser -> embedBuilder
                            .addField(resourceBundle.getString("command.pathogen.stats.max"),
                                    String.valueOf(pathogenUser.getInfectionLevel()), false));
        } else {
            embedBuilder.appendDescription(resourceBundle.getString("command.pathogen.notenabled"));
        }
        return new MessageBuilder(embedBuilder).build();
    }

    private List<String> getUserInfectionLevel(MessageReceivedEvent event) {
        List<String> reactions = new ArrayList<>();
        val pathogenUser = pathogenUserRepository.getByUserIdAndServerId(event.getAuthor()
                .getId(), event.getGuild().getId());
        if (pathogenUser != null) {
            if (pathogenUser.getVaccinated()) {
                reactions.add(Emojis.getRandomDoctorEmoji());
                reactions.add(Emojis.getNumberMap().get(0));
                reactions.add(Emojis.THUMBS_UP);
            } else if (pathogenUser.getInfectionLevel() == 0) {
                reactions.add(Emojis.HAPPY_FACE);
                reactions.add(Emojis.getNumberMap().get(0));
            } else if (pathogenUser.getInfectionLevel() > 0) {
                reactions.add(Emojis.SICK_FACE);
                reactions.add(Emojis.getNumberMap().get(pathogenUser.getInfectionLevel()));
            }
        }
        return reactions;
    }
}
