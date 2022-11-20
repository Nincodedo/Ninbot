package dev.nincodedo.ninbot.ocw;

import dev.nincodedo.nincord.command.slash.SlashCommand;
import dev.nincodedo.nincord.release.ReleaseType;

public interface OcwSlashCommand extends SlashCommand {
    @Override
    default ReleaseType getReleaseType() {
        return ReleaseType.ALPHA;
    }
}
