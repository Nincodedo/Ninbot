package dev.nincodedo.ninbot.common;

import dev.nincodedo.ninbot.common.release.ReleaseType;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

@UtilityClass
public class DegreesOfNinbot {

    public static boolean releaseAllowed(ReleaseType releaseType, Guild guild) {
        var allowedRelease = degreeCalculation(guild.getJDA().getShardManager(), guild.getOwner().getUser(), guild);
        //if the ReleaseType is PUBLIC or the allowedRelease and releaseType are the same, then this is allowed.
        if (ReleaseType.PUBLIC == releaseType || releaseType == allowedRelease) {
            return true;
        }
        return ReleaseType.ALPHA == allowedRelease && ReleaseType.BETA == releaseType;
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
