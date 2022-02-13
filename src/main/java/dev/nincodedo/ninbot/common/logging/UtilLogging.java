package dev.nincodedo.ninbot.common.logging;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

@UtilityClass
public class UtilLogging {

    public static final String NAME_ID_ENTITY_FORMAT = "%s(%s)";

    /**
     * Returns the guild name and id formatted for logging.
     *
     * @param guild
     * @return
     */
    public static String logGuildName(Guild guild) {
        if (guild == null) {
            return "null guild";
        }
        return String.format(NAME_ID_ENTITY_FORMAT, guild.getName(), guild.getId());
    }

    public static String logGuildInfo(Guild guild) {
        if (guild == null) {
            return "null guild";
        }
        return String.format("Server %s, Owner %s", logGuildName(guild), logMemberInfo(guild.retrieveOwner()
                .complete()));
    }

    public static String logMemberInfo(Member member) {
        return String.format(NAME_ID_ENTITY_FORMAT, member.getEffectiveName(), member.getId());
    }

    public static String logChannelInfo(Channel channel) {
        if (channel == null) {
            return "null channel";
        }
        return String.format(NAME_ID_ENTITY_FORMAT, channel.getName(), channel.getId());
    }
}
