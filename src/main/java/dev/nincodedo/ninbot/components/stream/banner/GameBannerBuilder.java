package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.ninbot.common.StreamUtils;
import org.slf4j.Logger;

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

public interface GameBannerBuilder {

    GameBannerRepository getGameBannerRepository();

    Logger log();

    default CompletableFuture<GameBanner> getGameBannerAsync(String gameTitle) {
        CompletableFuture<GameBanner> futureGameBanner = new CompletableFuture<GameBanner>().completeOnTimeout(null,
                20, TimeUnit.SECONDS);
        getExecutorService().submit(() -> {
            if (gameTitle == null) {
                futureGameBanner.complete(null);
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
                    } else if (gameBannerOptional.isPresent() && gameBannerOptional.get().getScore() < 0) {
                        Files.deleteIfExists(cachedBannerFile.toPath());
                    }
                }
            }
            futureGameBanner.complete(generateGameBannerFromTitle(gameTitle));
            return null;
        });
        return futureGameBanner;
    }

    private Optional<GameBanner> getGameBannerFromFile(File cachedBannerFile) {
        int logoId;
        int backgroundId;
        try {
            logoId = Integer.parseInt(cachedBannerFile.getName().split("-")[2]);
            backgroundId = Integer.parseInt(cachedBannerFile.getName().split("-")[3].split("\\.")[0]);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        return getGameBannerRepository().findGameBannerByLogoIdAndBackgroundId(logoId,
                backgroundId);
    }

    ExecutorService getExecutorService();

    GameBanner generateGameBannerFromTitle(String gameTitle);

    default List<File> getGameBannerFilesFromCache(String gameTitle) {
        var cacheFolder = new File("cache");
        if (!cacheFolder.exists()) {
            var folderMade = cacheFolder.mkdir();
            log().trace("Cache folder did not exist, made folder: {}", folderMade);
            return new ArrayList<>();
        }
        var cachedFileName = getCachedFileName(gameTitle);
        return Arrays.stream(Objects.requireNonNull(cacheFolder.listFiles((dir, name) -> name.startsWith(cachedFileName))))
                .toList();
    }

    default String getCachedFileName(String gameTitle) {
        if (gameTitle == null) {
            return null;
        } else {
            return gameTitle.replaceAll("[^A-Za-z0-9]", "");
        }
    }
}
