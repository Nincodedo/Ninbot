package dev.nincodedo.ninbot.components.pathogen;

import dev.nincodedo.ninbot.common.StreamUtils;
import dev.nincodedo.ninbot.components.pathogen.user.PathogenUserService;
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
        for (var guild : shardManager.getGuilds()) {
            var roleList = guild.getRolesByName(PathogenConfig.getVACCINATED_ROLE_NAME(), false);
            if (!roleList.isEmpty()) {
                var vaccinatedRole = roleList.get(0);
                guild.getMembers()
                        .stream()
                        .filter(member -> !member.getRoles().contains(vaccinatedRole))
                        .min(StreamUtils.shuffle())
                        .ifPresent(member -> guild.addRoleToMember(member, vaccinatedRole).reason("Random vaccination")
                                .queue(success -> {
                                    pathogenUserService.vaccinateUser(member.getId(), guild.getId());
                                    var infectedRoleList =
                                            guild.getRolesByName(PathogenConfig.getINFECTED_ROLE_NAME(), false);
                                    if (!infectedRoleList.isEmpty()) {
                                        guild.removeRoleFromMember(member, infectedRoleList.get(0)).queue();
                                    }
                                }));
            }
        }
    }
}
