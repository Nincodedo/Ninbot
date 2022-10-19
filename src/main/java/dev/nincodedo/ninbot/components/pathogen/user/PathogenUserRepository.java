package dev.nincodedo.ninbot.components.pathogen.user;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

public interface PathogenUserRepository extends BaseRepository<PathogenUser> {
    default PathogenUser getByUserIdAndServerId(String userId, String serverId) {
        return getByUserIdAndServerIdAndDeleted(userId, serverId, false);
    }

    PathogenUser getByUserIdAndServerIdAndDeleted(String userId, String serverId, Boolean isDeleted);

}
