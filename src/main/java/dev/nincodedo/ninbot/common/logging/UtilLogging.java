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
     * @param guild JDA guild
     * @return String of guild name(guild id)
     */
    public static String logGuildName(Guild guild) {
        if (guild == null) {
            return "null guild";
        }
        return String.format(NAME_ID_ENTITY_FORMAT, guild.getName(), guild.getId());
    }

    /**
     * Returns the guild name, id and owner name, id formatted for logging.
     *
     * @param guild JDA guild
     * @return String of guild name(guild id), owner name(owner id)
     */
    public static String logGuildInfo(Guild guild) {
        if (guild == null) {
            return "null guild";
        }
        return String.format("Server %s, Owner %s", logGuildName(guild), logMemberInfo(guild.retrieveOwner()
                .complete()));
    }

    /**
     * Returns the member name and id formatted for logging.
     *
     * @param member JDA member
     * @return String of member name(member id)
     */
    public static String logMemberInfo(Member member) {
        return String.format(NAME_ID_ENTITY_FORMAT, member.getEffectiveName(), member.getId());
    }

    /**
     * Returns the channel name and id formatted for logging.
     *
     * @param channel JDA channel
     * @return String of channel name(channel id)
     */
    public static String logChannelInfo(Channel channel) {
        if (channel == null) {
            return "null channel";
        }
        return String.format(NAME_ID_ENTITY_FORMAT, channel.getName(), channel.getId());
    }
}
