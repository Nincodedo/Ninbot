package dev.nincodedo.ninbot.components.stream.banner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public interface GameBannerBuilder {

    GameBannerRepository getGameBannerRepository();

    default CompletableFuture<GameBanner> getGameBannerAsync(String gameTitle) {
        CompletableFuture<GameBanner> futureGameBanner = new CompletableFuture<GameBanner>().completeOnTimeout(null,
                10, TimeUnit.SECONDS);
        Executors.newCachedThreadPool().submit(() -> {
            var cachedBanner = getGameBannerFilesFromCache(gameTitle);
            if (!cachedBanner.isEmpty()) {
                for (var cachedBannerFile : cachedBanner) {
                    int logoId = Integer.parseInt(cachedBannerFile.getName().split("-")[1]);
                    int backgroundId = Integer.parseInt(cachedBannerFile.getName().split("-")[2]);
                    var gameBannerOptional = getGameBannerRepository().findGameBannerByLogoIdAndBackgroundId(logoId,
                            backgroundId);
                    if (gameBannerOptional.isPresent() && gameBannerOptional.get().getScore() >= 0) {
                        var gameBanner = gameBannerOptional.get();
                        gameBanner.setFile(cachedBannerFile);
                        futureGameBanner.complete(gameBanner);
                    }
                }

            } else {
                futureGameBanner.complete(generateGameBannerFromTitle(gameTitle));
            }
            return null;
        });
        return futureGameBanner;
    }

    default List<GameBanner> getBadBanners(String gameTitle) {
        return getGameBannerRepository().findAllByGameTitle(gameTitle)
                .stream()
                .filter(gameBanner -> gameBanner.getScore() < 0)
                .toList();
    }

    GameBanner generateGameBannerFromTitle(String gameTitle);

    default List<File> getGameBannerFilesFromCache(String gameTitle) {
        var cachedFileName = getCachedFileName(gameTitle);
        var cacheFolder = new File("cache");
        if (!cacheFolder.exists()) {
            cacheFolder.mkdir();
            return new ArrayList<>();
        }
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
