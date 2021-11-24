package dev.nincodedo.ninbot.common;

import dev.nincodedo.ninbot.common.release.ReleaseType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

public class DegreesOfNinbot {

    private DegreesOfNinbot() {
        //no-op
    }

    public static boolean releaseAllowed(ReleaseType releaseType, Guild guild) {
        var allowedRelease = degreeCalculation(guild.getJDA().getShardManager(), guild.getOwner().getUser(), guild);
        //if the ReleaseType is PUBLIC or the allowedRelease and releaseType are the same, then this is allowed.
        if (ReleaseType.PUBLIC.equals(releaseType) || releaseType.equals(allowedRelease)) {
            return true;
        }
        return ReleaseType.ALPHA.equals(allowedRelease) && ReleaseType.BETA.equals(releaseType);
    }

    public static ReleaseType degreeCalculation(ShardManager shardManager, User targetUser, Guild targetGuild) {
        if (targetGuild.getId().equals(Constants.OCW_SERVER_ID)) {
            return ReleaseType.ALPHA;
        }
        var ocwGuild = shardManager.getGuildById(Constants.OCW_SERVER_ID);
        if (ocwGuild != null && ocwGuild.getMember(targetUser) != null) {
            return ReleaseType.BETA;
        }
        return ReleaseType.PUBLIC;
    }
}
