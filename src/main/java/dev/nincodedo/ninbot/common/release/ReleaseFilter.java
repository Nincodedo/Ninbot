package dev.nincodedo.ninbot.common.release;

import net.dv8tion.jda.api.entities.Guild;

public interface ReleaseFilter {
    /**
     * Determines if the release type is allowed for the given Guild.
     *
     * @param releaseType the release type
     * @param guild       the guild
     * @return true if it is allowed, false otherwise
     */
    boolean filter(ReleaseType releaseType, Guild guild);
}
