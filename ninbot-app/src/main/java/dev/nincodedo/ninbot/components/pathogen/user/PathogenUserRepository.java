package dev.nincodedo.ninbot.components.pathogen.user;

import dev.nincodedo.nincord.persistence.BaseRepository;

public interface PathogenUserRepository extends BaseRepository<PathogenUser> {
    PathogenUser getByUserIdAndServerId(String userId, String serverId);
}
