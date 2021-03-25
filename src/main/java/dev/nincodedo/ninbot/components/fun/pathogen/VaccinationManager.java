package dev.nincodedo.ninbot.components.fun.pathogen;

import dev.nincodedo.ninbot.components.common.StreamUtils;
import dev.nincodedo.ninbot.components.fun.pathogen.user.PathogenUserService;
import lombok.val;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VaccinationManager {

    private ShardManager shardManager;
    private PathogenUserService pathogenUserService;

    public VaccinationManager(ShardManager shardManager, PathogenUserService pathogenUserService) {
        this.shardManager = shardManager;
        this.pathogenUserService = pathogenUserService;
    }

    @Scheduled(cron = "0 0 0 * * SUN")
    public void randomVaccination() {
        for (val guild : shardManager.getGuilds()) {
            val roleList = guild.getRolesByName(PathogenConfig.getVACCINATED_ROLE_NAME(), false);
            if (!roleList.isEmpty()) {
                val vaccinatedRole = roleList.get(0);
                guild.getMembers()
                        .stream()
                        .filter(member -> !member.getRoles().contains(vaccinatedRole))
                        .min(StreamUtils.shuffle())
                        .ifPresent(member -> guild.addRoleToMember(member, vaccinatedRole)
                                .queue(success -> pathogenUserService.vaccinateUser(member.getId(), guild.getId())));
            }
        }
    }
}
