package dev.nincodedo.ninbot.common.release;

import net.dv8tion.jda.api.entities.Guild;

public class DefaultReleaseFilter implements ReleaseFilter {
    @Override
    public boolean filter(ReleaseType releaseType, Guild guild) {
        return true;
    }
}
