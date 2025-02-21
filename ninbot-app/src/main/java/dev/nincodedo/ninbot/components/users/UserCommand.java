package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.ninbot.components.users.UserCommandName.Subcommand;
import dev.nincodedo.nincord.command.slash.SlashSubCommand;
import dev.nincodedo.nincord.config.db.component.ComponentConfiguration;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.SlashCommandEventMessageExecutor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCommand implements SlashSubCommand<Subcommand> {

    private final UserService userService;
    private final ComponentService componentService;

    @Override
    public MessageExecutor execute(@NotNull SlashCommandInteractionEvent event,
            @NotNull SlashCommandEventMessageExecutor messageExecutor, @NotNull Subcommand subcommand) {
        if (subcommand == Subcommand.FEATURES) {
            event.deferReply(true).queue();
            var userToggleableComponents = componentService.findUserToggleableComponents();

            var usersDisabledComponents = userService.getUserById(event.getUser().getId()).getUserSettings()
                    .stream()
                    .filter(ComponentConfiguration::getDisabled)
                    .map(ComponentConfiguration::getComponent)
                    .map(component -> SelectOption.of(createSelectOptionLabel(component),
                            createSelectOptionValue(component)))
                    .toList();

            var selectOptions = userToggleableComponents.stream()
                    .map(component -> SelectOption.of(createSelectOptionLabel(component),
                            createSelectOptionValue(component)))
                    .toList();

            var editedMessage = new MessageEditBuilder().setReplace(true)
                    .setEmbeds(new EmbedBuilder().setTitle("Ninbot Feature User Settings")
                            .appendDescription("This is a list of all Ninbot features you currently have disabled. "
                                    + "Add items to the list to disable those features for your user in any server "
                                    + "with Ninbot.")
                            .build())
                    .setComponents(ActionRow.of(StringSelectMenu.create("user-disabled-id")
                            .addOptions(selectOptions)
                            .setRequiredRange(0, userToggleableComponents.size())
                            .setDefaultOptions(usersDisabledComponents)
                            .build()))
                    .build();

            event.getHook().editOriginal(editedMessage).queue();
        }
        return messageExecutor;
    }

    private @NotNull String createSelectOptionValue(dev.nincodedo.nincord.config.db.component.Component component) {
        return String.format("component-%s-%s", component.getName().replace('-', '_'),
                component.getId());
    }

    private String createSelectOptionLabel(dev.nincodedo.nincord.config.db.component.Component component) {
        return WordUtils.capitalizeFully(component.getName()
                .replace('-', ' '));
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
