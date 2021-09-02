package dev.nincodedo.ninbot.components.config.component;

import dev.nincodedo.ninbot.common.RolePermission;
import dev.nincodedo.ninbot.common.message.MessageAction;
import dev.nincodedo.ninbot.components.command.AbstractCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.apache.commons.text.WordUtils;

import java.util.Comparator;

public class ComponentCommand extends AbstractCommand {

    private ComponentService componentService;

    ComponentCommand(ComponentService componentService) {
        name = "component";
        length = 3;
        checkExactLength = false;
        this.componentService = componentService;
        this.permissionLevel = RolePermission.ADMIN;
    }

    //TODO implement SlashCommand
    @Override
    protected MessageAction executeCommand(PrivateMessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);

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
