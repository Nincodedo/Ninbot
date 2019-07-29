package com.nincraft.ninbot.components.config.component;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.RolePermission;
import lombok.val;
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
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "list":
                commandResult.addChannelAction(listComponents(event.getGuild().getId()));
                break;
            case "disable":
                disableComponent(event);
                commandResult.addSuccessfulReaction();
                break;
            case "enable":
                enableComponent(event);
                commandResult.addSuccessfulReaction();
                break;
            default:
                commandResult = displayHelp(event);
                break;
        }
        return commandResult;
    }

    private void enableComponent(MessageReceivedEvent event) {
        val componentName = event.getMessage().getContentStripped().split(" ")[3];
        componentService.enableComponent(componentName, event.getGuild().getId());
    }

    private void disableComponent(MessageReceivedEvent event) {
        val componentName = event.getMessage().getContentStripped().split(" ")[3];
        componentService.disableComponent(componentName, event.getGuild().getId());
    }

    private Message listComponents(String serverId) {
        val components = componentService.getAllComponents();
        val disabledComponents = componentService.getDisabledComponents(serverId);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        components.sort(Comparator.comparing(com.nincraft.ninbot.components.config.component.Component::getName));
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
