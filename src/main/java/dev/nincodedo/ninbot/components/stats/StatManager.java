package dev.nincodedo.ninbot.components.stats;

import io.micrometer.core.instrument.util.NamedThreadFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class StatManager {

    private static final org.apache.logging.log4j.Logger log =
            org.apache.logging.log4j.LogManager.getLogger(StatManager.class);
    private StatRepository statRepository;
    private ExecutorService executorService;

    public StatManager(StatRepository statRepository) {
        this.statRepository = statRepository;
        this.executorService = Executors.newCachedThreadPool(new NamedThreadFactory("stat-manager"));
    }

    public void recordCount(String name, String category, String serverId, int count) {
        executorService.execute(() -> {
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

    public void addCount(String name, String category, String serverId, int count) {
        executorService.execute(() -> {
            final java.util.Optional<dev.nincodedo.ninbot.components.stats.Stat> optionalStat =
                    statRepository.findByNameAndCategoryAndServerId(name, category, serverId);
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

    public CompletableFuture<List<Stat>> getStatByCategoryAndServerId(String category, String serverId) {
        CompletableFuture<List<Stat>> future = new CompletableFuture<>();
        executorService.submit(() -> {
            final java.util.List<dev.nincodedo.ninbot.components.stats.Stat> statList =
                    statRepository.findByCategoryAndServerId(category, serverId);
            future.complete(statList);
            return null;
        });
        return future;
    }

    public CompletableFuture<List<Stat>> getStatByServerId(String serverId) {
        CompletableFuture<List<Stat>> future = new CompletableFuture<>();
        executorService.submit(() -> {
            final java.util.List<dev.nincodedo.ninbot.components.stats.Stat> statList =
                    statRepository.findByServerId(serverId);
            future.complete(statList);
            return null;
        });
        return future;
    }

    public Map<String, List<Stat>> getStatMapByServerId(String serverId) {
        final java.util.concurrent.CompletableFuture<java.util.List<dev.nincodedo.ninbot.components.stats.Stat>> future = getStatByServerId(serverId);
        Map<String, List<Stat>> statMap = new HashMap<>();
        try {
            final java.util.List<dev.nincodedo.ninbot.components.stats.Stat> statsList = future.get();
            for (final dev.nincodedo.ninbot.components.stats.Stat stat : statsList) {
                if (!statMap.containsKey(stat.getCategory())) {
                    statMap.put(stat.getCategory(), new ArrayList<>());
                }
                final java.util.List<dev.nincodedo.ninbot.components.stats.Stat> mapList =
                        statMap.get(stat.getCategory());
                mapList.add(stat);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get stat map", e);
        }
        return statMap;
    }

    public CompletableFuture<List<Stat>> getStatByCategory(String category) {
        CompletableFuture<List<Stat>> future = new CompletableFuture<>();
        executorService.submit(() -> {
            final java.util.List<dev.nincodedo.ninbot.components.stats.Stat> statList =
                    statRepository.findByCategory(category);
            future.complete(statList);
            return null;
        });
        return future;
    }
}
