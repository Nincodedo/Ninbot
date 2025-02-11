package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.ninbot.components.users.UserCommandName.Subcommand;
import dev.nincodedo.nincord.command.slash.SlashSubCommand;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.config.db.component.DisabledComponents;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.SlashCommandEventMessageExecutor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class UserCommand implements SlashSubCommand<Subcommand> {

    private final ComponentService componentService;

    @Override
    public MessageExecutor execute(@NotNull SlashCommandInteractionEvent event,
            @NotNull SlashCommandEventMessageExecutor messageExecutor, @NotNull Subcommand subcommand) {
        if (subcommand == Subcommand.FEATURES) {
            event.deferReply(true).queue();
            var userToggleableComponents = componentService.findUserToggleableComponents();
            var usersDisabledComponents = componentService.getDisabledComponentsByUser(event.getUser().getId())
                    .stream()
                    .map(DisabledComponents::getComponent)
                    .map(component -> SelectOption.of(WordUtils.capitalizeFully(component.getName()
                            .replace('-', ' ')), String.format("component-%s-%s", component.getName().replace('-', '_'),
                            component.getId())))
                    .toList();

            var selectOptions = userToggleableComponents.stream()
                    .map(component -> SelectOption.of(WordUtils.capitalizeFully(component.getName()
                            .replace('-', ' ')), String.format("component-%s-%s", component.getName().replace('-', '_'),
                            component.getId())))
                    .toList();

            event.getHook()
                    .editOriginal("This is a list of all Ninbot features you currently have disabled. Add items to "
                            + "the list to disable those features for your user on any server with Ninbot.")
                    .setComponents(ActionRow.of(StringSelectMenu.create("user-disabled-id")
                            .addOptions(selectOptions)
                            .setRequiredRange(0, userToggleableComponents.size())
                            .setDefaultOptions(usersDisabledComponents)
                            .build()))
                    .queue();
        }
        return messageExecutor;
    }

    @Override
    public String getName() {
        return UserCommandName.USER.get();
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Arrays.asList(new SubcommandData(Subcommand.FEATURES.get(), "Opt in or out of various Ninbot "
                + "features."));
    }

    @Override
    public Class<Subcommand> enumSubcommandClass() {
        return Subcommand.class;
    }
}
