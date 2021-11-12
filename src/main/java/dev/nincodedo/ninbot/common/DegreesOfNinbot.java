package dev.nincodedo.ninbot.common;

import dev.nincodedo.ninbot.common.release.ReleaseType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class DegreesOfNinbot {
    public ReleaseType degreeCalculation(JDA jda, User targetUser, Guild targetGuild) {
        if (targetGuild.getId().equals(Constants.OCW_SERVER_ID)) {
            return ReleaseType.ALPHA;
        }
        var ocwGuild = jda.getGuildById(Constants.OCW_SERVER_ID);
        if (ocwGuild != null && ocwGuild.getMember(targetUser) != null) {
            return ReleaseType.BETA;
        }
        return ReleaseType.PUBLIC;
    }
}
