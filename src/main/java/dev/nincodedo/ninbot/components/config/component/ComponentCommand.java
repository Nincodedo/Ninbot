package dev.nincodedo.ninbot.components.config.component;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.RolePermission;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class ComponentCommand extends AbstractCommand {

    private ComponentService componentService;

    ComponentCommand(ComponentService componentService) {
        name = "component";
        length = 3;
        checkExactLength = false;
        this.componentService = componentService;
        this.permissionLevel = RolePermission.ADMIN;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        var message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "list" -> messageAction.addChannelAction(listComponents(event.getGuild().getId()));
            case "disable" -> {
                disableComponent(event);
                messageAction.addSuccessfulReaction();
            }
            case "enable" -> {
                enableComponent(event);
                messageAction.addSuccessfulReaction();
            }
            default -> messageAction = displayHelp(event);
        }
        return messageAction;
    }

    private void enableComponent(MessageReceivedEvent event) {
        var componentName = getSubcommand(event.getMessage().getContentStripped(), 3);
        componentService.enableComponent(componentName, event.getGuild().getId());
    }

    private void disableComponent(MessageReceivedEvent event) {
        var componentName = getSubcommand(event.getMessage().getContentStripped(), 3);
        componentService.disableComponent(componentName, event.getGuild().getId());
    }

    private Message listComponents(String serverId) {
        var components = componentService.getAllComponents();
        var disabledComponents = componentService.getDisabledComponents(serverId);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        components.sort(Comparator.comparing(dev.nincodedo.ninbot.components.config.component.Component::getName));
        components.forEach(component -> {
            String enabledText = resourceBundle.getString("command.component.enabled");
            for (DisabledComponents disabledComponent : disabledComponents) {
                if (disabledComponent.getComponent().equals(component)) {
                    enabledText = resourceBundle.getString("command.component.disabled");
                }
            }
            embedBuilder.addField(
                    component.getName() + " - " + enabledText, WordUtils.capitalizeFully(component.getType()
                            .toString()), true);
        });
        embedBuilder.setTitle(resourceBundle.getString("command.component.listtitle"));
        return new MessageBuilder(embedBuilder).build();
    }
}
