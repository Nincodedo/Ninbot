package dev.nincodedo.ninbot.components.dab;

import org.springframework.stereotype.Component;

@Component
public class HugeDabMessageInteraction extends DabMessageInteraction {
    HugeDabMessageInteraction(HugeDabber hugeDabber) {
        super(hugeDabber);
    }

    @Override
    public String getName() {
        return DabCommandName.HUGEDAB.get();
    }
}
