package dev.nincodedo.ninbot.components.subscribe;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.config.db.ConfigConstants;
import dev.nincodedo.ninbot.common.config.db.ConfigService;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import dev.nincodedo.ninbot.components.pathogen.PathogenConfig;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
public class SubscribeCommand implements SlashCommand {

    private ConfigService configService;

    public SubscribeCommand(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public MessageExecutor execute(@NotNull SlashCommandInteractionEvent event,
            @NotNull SlashCommandEventMessageExecutor messageExecutor) {
        messageExecutor.deferEphemeralReply();
        var guild = event.getGuild();
        var role = event.getOption(SubscribeCommandName.Option.SUBSCRIPTION.get(), OptionMapping::getAsRole);
        if (guild != null && isValidSubscribeRole(role, guild.getId())) {
            try {
                addOrRemoveSubscription(event.getInteraction().getHook(), event.getMember(), guild, role);
            } catch (PermissionException e) {
                event.getInteraction().getHook().editOriginal(Emojis.CROSS_X).queue();
            }
        } else {
            messageExecutor.addEphemeralMessage(resourceBundle().getString(""));
        }
        return messageExecutor;
    }

    void addOrRemoveSubscription(InteractionHook interactionHook, Member member, Guild guild,
            Role role) throws PermissionException {
        guild.addRoleToMember(member, role).queue(successAction(interactionHook), failureAction(interactionHook));
    }

    @NotNull
    Consumer<Throwable> failureAction(InteractionHook interactionHook) {
        return failure -> interactionHook.editOriginal(Emojis.CROSS_X).queue();
    }

    @NotNull
    Consumer<Void> successAction(InteractionHook interactionHook) {
        return success -> interactionHook.editOriginal(Emojis.CHECK_MARK).queue();
    }

    private boolean isValidSubscribeRole(Role role, String serverId) {
        List<String> roleDenyList = configService.getValuesByName(serverId, ConfigConstants.ROLE_DENY_LIST);
        roleDenyList.add(PathogenConfig.getINFECTED_ROLE_NAME());
        roleDenyList.add(PathogenConfig.getVACCINATED_ROLE_NAME());
        return role != null && !roleDenyList.contains(role.getName());
    }

    @Override
    public String getName() {
        return SubscribeCommandName.SUBSCRIBE.get();
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(new OptionData(OptionType.ROLE, SubscribeCommandName.Option.SUBSCRIPTION.get(), "Role you want"
                + " subscribe to.", true));
    }
}
