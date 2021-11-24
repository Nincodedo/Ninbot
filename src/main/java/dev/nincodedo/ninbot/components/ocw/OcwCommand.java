package dev.nincodedo.ninbot.components.ocw;

import dev.nincodedo.ninbot.common.command.SlashCommand;
import dev.nincodedo.ninbot.common.release.ReleaseType;

public interface OcwCommand extends SlashCommand {
    @Override
    default ReleaseType getReleaseType() {
        return ReleaseType.ALPHA;
    }
}
