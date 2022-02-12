package dev.nincodedo.ninbot.common.logging;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

@UtilityClass
public class UtilLogging {
    public static String logGuildName(Guild guild) {
        return String.format("%s(%s)", guild.getName(), guild.getId());
    }

    public static String logGuildInfo(Guild guild) {
        return String.format("Server %s, Owner %s", logGuildName(guild), logMemberInfo(guild.retrieveOwner()
                .complete()));
    }

    private static String logMemberInfo(Member member) {
        return String.format("%s(%s)", member.getEffectiveName(), member.getId());
    }
}
