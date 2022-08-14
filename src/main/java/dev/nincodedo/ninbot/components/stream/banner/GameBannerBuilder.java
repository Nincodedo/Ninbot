package dev.nincodedo.ninbot.components.stream.banner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface GameBannerBuilder {

    GameBannerRepository getGameBannerRepository();

    default Optional<GameBanner> getGameBanner(String gameTitle) {
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
                    return Optional.of(gameBanner);
                }
            }
            return Optional.empty();
        } else {
            return generateGameBannerFromTitle(gameTitle);
        }
    }

    default List<GameBanner> getBadBanners(String gameTitle) {
        return getGameBannerRepository().findAllByGameTitle(gameTitle)
                .stream()
                .filter(gameBanner -> gameBanner.getScore() < 0)
                .toList();
    }

    Optional<GameBanner> generateGameBannerFromTitle(String gameTitle);

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
