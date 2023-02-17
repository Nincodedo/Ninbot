package dev.nincodedo.nincord.stats;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatManager {

    private StatRepository statRepository;

    public StatManager(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    /**
     * Sets the count to the stat if it exists. Creates the stat if it does not exist and sets the count.
     *
     * @param name     The name of the stat.
     * @param category The category of the stat.
     * @param serverId The server ID of the server that the stat is being updated for.
     * @param count    The count of the stat.
     */
    public void upsertCount(String name, String category, String serverId, int count) {
        statRepository.findByNameAndCategoryAndServerId(name, category, serverId).ifPresentOrElse(stat -> {
            stat.setCount(count);
            statRepository.save(stat);
        }, () -> {
            Stat stat = new Stat();
            stat.setName(name);
            stat.setCategory(category);
            stat.setServerId(serverId);
            stat.setCount(count);
            statRepository.save(stat);
        });
    }

    public void addOneCount(String name, String category, String serverId) {
        addCount(name, category, serverId, 1);
    }

    /**
     * Adds the count to the stat if it exists. Creates the stat if it does not exist and sets the count.
     *
     * @param name     The name of the stat.
     * @param category The category of the stat.
     * @param serverId The server ID of the server that the stat is being updated for.
     * @param count    The count of the stat.
     */
    public void addCount(String name, String category, String serverId, int count) {
        var stat = statRepository.findByNameAndCategoryAndServerId(name, category, serverId)
                .orElse(new Stat(name, category, serverId));
        stat.setCount(stat.getCount() + count);
        statRepository.save(stat);
    }

}
