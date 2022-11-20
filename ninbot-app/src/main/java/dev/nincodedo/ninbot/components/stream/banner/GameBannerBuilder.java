package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.nincord.StreamUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class GameBannerBuilder {

    protected GameBannerRepository gameBannerRepository;
    protected ExecutorService executorService;

    protected GameBannerBuilder(GameBannerRepository gameBannerRepository, ExecutorService executorService) {
        this.gameBannerRepository = gameBannerRepository;
        this.executorService = executorService;
    }

    public CompletableFuture<GameBanner> getGameBannerAsync(String gameTitle) {
        CompletableFuture<GameBanner> futureGameBanner = new CompletableFuture<GameBanner>().completeOnTimeout(null,
                20, TimeUnit.SECONDS);
        executorService.submit(() -> {
            if (gameTitle == null) {
                futureGameBanner.complete(null);
                return null;
            }
            var cachedBanners = getGameBannerFilesFromCache(gameTitle).stream()
                    .sorted(StreamUtils.shuffle())
                    .toList();
            if (!cachedBanners.isEmpty()) {
                for (var cachedBannerFile : cachedBanners) {
                    var gameBannerOptional = getGameBannerFromFile(cachedBannerFile);
                    if (gameBannerOptional.isPresent() && gameBannerOptional.get().getScore() >= 0) {
                        var gameBanner = gameBannerOptional.get();
                        gameBanner.setFile(cachedBannerFile);
                        futureGameBanner.complete(gameBanner);
                        return null;
                    } else if (gameBannerOptional.isPresent()) {
                        Files.deleteIfExists(cachedBannerFile.toPath());
                    }
                }
            }
            futureGameBanner.complete(generateGameBannerFromTitle(gameTitle));
            return null;
        });
        return futureGameBanner;
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
        return gameBannerRepository.findGameBannerByLogoIdAndBackgroundId(logoId,
                backgroundId);
    }

    /**
     * Generates 3 GameBanners, returning only 1, or null if one could not be created. Others are stored in cache.
     *
     * @param gameTitle String title of a game
     * @return a GameBanner or null if one could not be created
     */
    protected abstract GameBanner generateGameBannerFromTitle(String gameTitle);

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
}
