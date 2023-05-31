package dev.nincodedo.ninbot.components.stream.banner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class BannerCleanup {

    private GameBannerRepository gameBannerRepository;

    public BannerCleanup(GameBannerRepository gameBannerRepository) {
        this.gameBannerRepository = gameBannerRepository;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS, initialDelay = 1)
    protected void deleteBadBannersFromCache() {
        List<Boolean> fileList = new ArrayList<>();
        gameBannerRepository.findAll()
                .stream()
                .filter(gameBanner -> gameBanner.getScore() < 0)
                .map(gameBanner -> new File("cache" + File.separatorChar + gameBanner.getFileName()))
                .forEach(file -> {
                    try {
                        fileList.add(Files.deleteIfExists(file.toPath()));
                    } catch (IOException e) {
                        log.error("Failed to delete {}", file, e);
                    }
                });
        long deletedFiles = fileList.stream().filter(aBoolean -> aBoolean).count();
        if (deletedFiles > 0) {
            log.trace("Cleared out {} file(s)", deletedFiles);
        }
    }

    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.DAYS, initialDelay = 30)
    protected void deleteAllBannersFromCache() {
        File cacheDirectory = new File("cache");
        if (cacheDirectory.exists()) {
            try {
                var fileList = cacheDirectory.listFiles();
                if (fileList != null) {
                    log.trace("Completely cleaning out banner cache directory of {} file(s)",
                            fileList.length);
                }
                FileUtils.cleanDirectory(cacheDirectory);
            } catch (IOException e) {
                log.error("Failed to clean cache directory", e);
            }
        }
    }
}
