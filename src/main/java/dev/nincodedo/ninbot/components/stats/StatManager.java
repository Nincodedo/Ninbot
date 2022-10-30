package dev.nincodedo.ninbot.components.stats;

import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
     * If the stat exists, update it. If it doesn't exist, create it.
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

    public CompletableFuture<List<Stat>> getStatByCategoryAndServerId(String category, String serverId) {
        CompletableFuture<List<Stat>> future = new CompletableFuture<>();
        executorService.submit(() -> {
            var statList = statRepository.findByCategoryAndServerId(category, serverId);
            future.complete(statList);
            return null;
        });
        return future;
    }

    public CompletableFuture<List<Stat>> getStatByServerId(String serverId) {
        CompletableFuture<List<Stat>> future = new CompletableFuture<>();
        executorService.submit(() -> {
            var statList = statRepository.findByServerId(serverId);
            future.complete(statList);
            return null;
        });
        return future;
    }

    public Map<String, List<Stat>> getStatMapByServerId(String serverId) {
        var future = getStatByServerId(serverId);
        Map<String, List<Stat>> statMap = new HashMap<>();
        try {
            var statsList = future.get();
            for (var stat : statsList) {
                if (!statMap.containsKey(stat.getCategory())) {
                    statMap.put(stat.getCategory(), new ArrayList<>());
                }
                var mapList = statMap.get(stat.getCategory());
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
            var statList = statRepository.findByCategory(category);
            future.complete(statList);
            return null;
        });
        return future;
    }
}
