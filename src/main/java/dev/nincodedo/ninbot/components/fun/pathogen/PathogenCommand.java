package dev.nincodedo.ninbot.components.fun.pathogen;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.Emojis;
import dev.nincodedo.ninbot.components.common.MessageAction;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
        val roleList = guild.getRolesByName(PathogenConfig.getROLE_NAME(), true);
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
            if (pathogenUser.getInfectionLevel() == 0) {
                reactions.add(Emojis.HAPPY_FACE);
            } else if (pathogenUser.getInfectionLevel() > 0) {
                reactions.add(Emojis.SICK_FACE);
            }
            reactions.add(Emojis.getNumberMap().get(pathogenUser.getInfectionLevel()));
        }
        return reactions;
    }
}
