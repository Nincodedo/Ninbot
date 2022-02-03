package dev.nincodedo.ninbot.components.dab;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HugeDabCommand extends DabCommand {
    public HugeDabCommand(Dabber dabber) {
        super(dabber);
    }

    @Override
    public String getName() {
        return DabCommandName.HUGEDAB.get();
    }
}
