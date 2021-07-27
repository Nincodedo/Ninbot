package dev.nincodedo.ninbot.components.fun.pathogen.user;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class PathogenUserService {
    private PathogenUserRepository pathogenUserRepository;

    public PathogenUserService(PathogenUserRepository pathogenUserRepository) {
        this.pathogenUserRepository = pathogenUserRepository;
    }

    public void uninfectedUser(PathogenUser pathogenUser, String userId, String guildId) {
        if (pathogenUser == null) {
            pathogenUser = new PathogenUser();
        }
        pathogenUser.setUserId(userId);
        pathogenUser.setInfectionLevel(0);
        pathogenUser.setServerId(guildId);
        pathogenUser.setLastInitialCure(new Date());
        pathogenUserRepository.save(pathogenUser);
    }

    public void infectedUser(PathogenUser pathogenUser, String userId, String guildId) {
        if (pathogenUser != null) {
            if (pathogenUser.getInfectionLevel() < 9) {
                pathogenUser.setInfectionLevel(pathogenUser.getInfectionLevel() + 1);
            }
        } else {
            pathogenUser = new PathogenUser();
            pathogenUser.setInfectionLevel(1);
            pathogenUser.setUserId(userId);
            pathogenUser.setLastInitialInfection(new Date());
        }
        pathogenUser.setServerId(guildId);
        pathogenUserRepository.save(pathogenUser);
    }

    public PathogenUser getByUserIdAndServerId(String userId, String serverId) {
        return pathogenUserRepository.getByUserIdAndServerId(userId, serverId);
    }

    public void vaccinateUser(String userId, String serverId) {
        PathogenUser pathogenUser = getByUserIdAndServerId(userId, serverId);
        if (pathogenUser == null) {
            pathogenUser = new PathogenUser();
        }
        pathogenUser.setVaccinated(true);
        pathogenUser.setInfectionLevel(0);
        pathogenUser.setLastInitialCure(new Date());
        pathogenUserRepository.save(pathogenUser);
    }
}
