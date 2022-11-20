package dev.nincodedo.ninbot.components.stream.banner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class BannerCleanup {

    private GameBannerRepository gameBannerRepository;

    public BannerCleanup(GameBannerRepository gameBannerRepository) {
        this.gameBannerRepository = gameBannerRepository;
    }

    @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS, initialDelay = 12)
    protected void deleteBadBannersFromCache() {
        gameBannerRepository.findAll()
                .stream()
                .filter(gameBanner -> gameBanner.getScore() < 0)
                .map(gameBanner -> new File("cache" + File.separatorChar + gameBanner.getFileName()))
                .forEach(file -> {
                    try {
                        Files.deleteIfExists(file.toPath());
                    } catch (IOException e) {
                        log.error("Failed to delete {}", file, e);
                    }
                });
    }

    @Scheduled(fixedDelay = 7, timeUnit = TimeUnit.DAYS, initialDelay = 7)
    protected void deleteAllBannersFromCache() {
        File cacheDirectory = new File("cache");
        if (cacheDirectory.exists()) {
            try {
                FileUtils.cleanDirectory(cacheDirectory);
            } catch (IOException e) {
                log.error("Failed to clean cache directory", e);
            }
        }
    }
}
