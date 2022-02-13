package dev.nincodedo.ninbot.common.release;

import net.dv8tion.jda.api.entities.Guild;

public interface ReleaseFilter {
    boolean filter(ReleaseType releaseType, Guild guild);
}
