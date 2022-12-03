package dev.nincodedo.nincord.stats;

import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class StatManager {

    private StatRepository statRepository;
    private ExecutorService executorService;

    public StatManager(StatRepository statRepository) {
        this.statRepository = statRepository;
        this.executorService = Executors.newCachedThreadPool(new NamedThreadFactory("stat-manager"));
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
        executorService.execute(() -> statRepository.findByNameAndCategoryAndServerId(name, category, serverId)
                .ifPresentOrElse(stat -> {
                    stat.setCount(count);
                    statRepository.save(stat);
                }, () -> {
                    Stat stat = new Stat();
                    stat.setName(name);
                    stat.setCategory(category);
                    stat.setServerId(serverId);
                    stat.setCount(count);
                    statRepository.save(stat);
                }));
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
        executorService.execute(() -> {
            var optionalStat = statRepository.findByNameAndCategoryAndServerId(name, category, serverId);
            Stat stat;
            if (optionalStat.isPresent()) {
                stat = optionalStat.get();
                stat.setCount(stat.getCount() + count);
            } else {
                stat = new Stat();
                stat.setName(name);
                stat.setCategory(category);
                stat.setCount(count);
                stat.setServerId(serverId);
            }
            statRepository.save(stat);
        });
    }

}
