package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.nincord.util.StreamUtils;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public abstract class GameBannerBuilder {

    protected GameBannerRepository gameBannerRepository;
    protected ExecutorService executorService;

    protected GameBannerBuilder(GameBannerRepository gameBannerRepository, ExecutorService executorService) {
        this.gameBannerRepository = gameBannerRepository;
        this.executorService = executorService;
    }

    @WithSpan
    public CompletableFuture<GameBanner> getGameBannerAsync(String gameTitle) {
        CompletableFuture<GameBanner> futureGameBanner = new CompletableFuture<GameBanner>().completeOnTimeout(null,
                20, TimeUnit.SECONDS);
        executorService.submit(() -> {
            if (gameTitle == null) {
                futureGameBanner.complete(null);
                return null;
            }
            var cachedBanners = getGameBannerFilesFromCache(gameTitle).stream()
                    .map(this::getGameBannerFromFile)
                    .flatMap(Optional::stream)
                    .sorted(StreamUtils.shuffle())
                    .toList();
            for (var cachedBanner : cachedBanners) {
                if (cachedBanner.getScore() >= 0) {
                    recordBannerUsed(cachedBanner);
                    futureGameBanner.complete(cachedBanner);
                    return null;
                } else {
                    Files.deleteIfExists(cachedBanner.getFile().toPath());
                }
            }
            var gameBanner = generateGameBannerFromTitle(gameTitle);
            recordBannerUsed(gameBanner);
            futureGameBanner.complete(gameBanner);
            return null;
        });
        return futureGameBanner;
    }

    private void recordBannerUsed(GameBanner gameBanner) {
        if (gameBanner != null) {
            gameBanner.setUses(gameBanner.getUses() + 1);
            gameBanner.setLastUse(LocalDateTime.now());
            gameBannerRepository.save(gameBanner);
        }
    }

    public Optional<GameBanner> getGameBannerFromFile(File cachedBannerFile) {
        int logoId;
        int backgroundId;
        try {
            logoId = GameBannerUtils.getLogoIdFromFile(cachedBannerFile);
            backgroundId = GameBannerUtils.getBackgroundIdFromFile(cachedBannerFile);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        var gameBannerOptional = gameBannerRepository.findGameBannerByLogoIdAndBackgroundId(logoId, backgroundId);
        gameBannerOptional.ifPresent(gameBanner -> gameBanner.setFile(cachedBannerFile));
        return gameBannerOptional;
    }

    /**
     * Generates zero to three GameBanners, returning only 1, or null if one could not be created. Others are stored
     * in cache.
     *
     * @param gameTitle String title of a game
     * @return a GameBanner or null if one could not be created
     */
    protected abstract @Nullable GameBanner generateGameBannerFromTitle(String gameTitle);

    public List<File> getGameBannerFilesFromCache(String gameTitle) {
        var cacheFolder = new File("cache");
        if (!cacheFolder.exists()) {
            var folderMade = cacheFolder.mkdir();
            log.trace("Cache folder did not exist, made folder: {}", folderMade);
            return new ArrayList<>();
        }
        var cachedFileName = GameBannerUtils.getCachedFileName(gameTitle);
        return Arrays.stream(Objects.requireNonNull(cacheFolder.listFiles((dir, name) -> name.startsWith(cachedFileName))))
                .toList();
    }

    /**
     * Gets the "best" banner to choose from a list of banners. "Best" is determined by a combination of user votes
     * and number of weeks since last use.
     *
     * @param gameBanners a possibly null list of game banners
     * @return a possibly empty optional
     */
    protected Optional<GameBanner> getMostSuitableBanner(@Nullable List<GameBanner> gameBanners) {
        if (gameBanners == null || gameBanners.isEmpty()) {
            return Optional.empty();
        } else if (gameBanners.size() == 1) {
            return Optional.of(gameBanners.get(0));
        }
        var scoredGameBannerMap = gameBanners.stream()
                .collect(Collectors.toMap(gameBanner -> Math.min(ChronoUnit.WEEKS.between(LocalDate.now(),
                        gameBanner.getLastUse() == null ? LocalDate.now()
                                .minusDays(7) : gameBanner.getLastUse()), gameBanner.getScore())
                        + gameBanner.getScore(), gameBanner -> gameBanner, (oldV, newV) -> oldV, LinkedHashMap::new));
        var scoredNotUsedTodayGameBannerMap = scoredGameBannerMap.entrySet()
                .stream()
                .filter(longGameBannerEntry -> longGameBannerEntry.getValue()
                        .getLastUse() == null || longGameBannerEntry.getValue()
                        .getLastUse()
                        .isBefore(LocalDate.now().atStartOfDay()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        var maxScore = scoredGameBannerMap.keySet().stream().max(Long::compareTo);
        if (!scoredNotUsedTodayGameBannerMap.isEmpty()) {
            var maxScoreNotToday = scoredNotUsedTodayGameBannerMap.keySet().stream().max(Long::compareTo);
            return Optional.of(scoredGameBannerMap.get(maxScoreNotToday.get()));
        } else if (maxScore.isPresent()) {
            return Optional.of(scoredGameBannerMap.get(maxScore.get()));
        }
        return gameBanners.stream().min(StreamUtils.shuffle());
    }
}
