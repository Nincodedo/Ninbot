package dev.nincodedo.ninbot.common.command.component;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;

import java.util.Optional;

public interface Interaction {

    default Optional<ComponentData> getComponentDataFromEvent(GenericComponentInteractionCreateEvent event) {
        var componentId = event.getInteraction().getComponentId();
        return getComponentData(componentId);
    }

    default Optional<ComponentData> getComponentDataFromEvent(ModalInteractionEvent event) {
        var componentId = event.getModalId();
        return getComponentData(componentId);
    }

    default Optional<ComponentData> getComponentData(String componentId) {
        if (componentId.contains("-")) {
            var splitId = componentId.split("-");
            if (splitId.length == 3) {
                return Optional.of(new ComponentData(splitId[0], splitId[1], splitId[2]));
            } else if (splitId.length == 2) {
                return Optional.of(new ComponentData(splitId[0], splitId[1], null));
            }
        }
        return Optional.empty();
    }
}
