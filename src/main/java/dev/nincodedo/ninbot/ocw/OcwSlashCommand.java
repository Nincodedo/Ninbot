package dev.nincodedo.ninbot.ocw;

import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.release.ReleaseType;

public interface OcwSlashCommand extends SlashCommand {
    @Override
    default ReleaseType getReleaseType() {
        return ReleaseType.ALPHA;
    }
}
