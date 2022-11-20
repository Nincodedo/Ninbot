package dev.nincodedo.ninbot.ocw.release;

import dev.nincodedo.ninbot.NinbotConstants;
import dev.nincodedo.nincord.release.ReleaseFilter;
import dev.nincodedo.nincord.release.ReleaseType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

public class DegreesOfNinbot implements ReleaseFilter {

    @Override
    public boolean filter(ReleaseType releaseType, Guild guild) {
        var allowedRelease = degreeCalculation(guild.getJDA().getShardManager(), guild.getOwner().getUser(), guild);
        //if the ReleaseType is PUBLIC or the allowedRelease and releaseType are the same, then this is allowed.
        if (ReleaseType.PUBLIC == releaseType || releaseType == allowedRelease) {
            return true;
        }
        return ReleaseType.ALPHA == allowedRelease && ReleaseType.BETA == releaseType;
    }

    private ReleaseType degreeCalculation(ShardManager shardManager, User targetUser, Guild targetGuild) {
        if (targetGuild.getId().equals(NinbotConstants.OCW_GUILD_ID)) {
            return ReleaseType.ALPHA;
        }
        var ocwGuild = shardManager.getGuildById(NinbotConstants.OCW_GUILD_ID);
        if (ocwGuild != null && ocwGuild.getMember(targetUser) != null) {
            return ReleaseType.BETA;
        }
        return ReleaseType.PUBLIC;
    }
}
