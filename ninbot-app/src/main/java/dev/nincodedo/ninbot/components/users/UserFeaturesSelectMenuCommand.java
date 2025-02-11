package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.nincord.command.component.ComponentData;
import dev.nincodedo.nincord.command.component.StringSelectMenuInteraction;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.StringSelectMenuInteractionCommandMessageExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFeaturesSelectMenuCommand implements StringSelectMenuInteraction {

    private final ComponentService componentService;

    @Override
    public String getName() {
        return UserCommandName.USER.get();
    }


    @Override
    public MessageExecutor execute(@NotNull StringSelectInteractionEvent event,
            @NotNull StringSelectMenuInteractionCommandMessageExecutor messageExecutor,
            @NotNull ComponentData componentData) {
        var disabledComponents = event.getSelectedOptions();
        var disabledComponentNames = disabledComponents.stream()
                .map(SelectOption::getValue)
                .map(value -> value.split("-")[1].replace('_', '-'))
                .toList();
        componentService.setDisabledComponentsByUser(event.getUser().getId(), disabledComponentNames);
        if (disabledComponents.isEmpty()) {
            event.editMessage("All Ninbot features are now enabled.").setComponents().queue();
        } else {
            var disabledComponentsString = disabledComponentNames.stream()
                    .map(name -> WordUtils.capitalizeFully(name.replace('-', ' ')))
                    .collect(Collectors.joining(", "));
            event.editMessage(String.format("The following Ninbot features are now disabled: %s",
                            disabledComponentsString)).setComponents()
                    .queue();
        }

        return messageExecutor;
    }

    @Override
    public Logger log() {
        return log;
    }
}
