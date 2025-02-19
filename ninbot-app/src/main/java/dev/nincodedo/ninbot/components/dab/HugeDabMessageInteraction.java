package dev.nincodedo.ninbot.components.dab;

import net.dv8tion.jda.api.interactions.IntegrationType;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class HugeDabMessageInteraction extends DabMessageInteraction {
    HugeDabMessageInteraction(HugeDabber hugeDabber) {
        super(hugeDabber);
    }

    @Override
    public String getName() {
        return DabCommandName.HUGEDAB.get();
    }

    @Override
    public Set<IntegrationType> allowedIntegrations() {
        return Set.of(IntegrationType.GUILD_INSTALL);
    }
}
