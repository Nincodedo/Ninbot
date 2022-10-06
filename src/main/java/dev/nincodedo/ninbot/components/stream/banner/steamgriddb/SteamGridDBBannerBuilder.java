package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import dev.nincodedo.ninbot.common.StreamUtils;
import dev.nincodedo.ninbot.components.stream.banner.GameBanner;
import dev.nincodedo.ninbot.components.stream.banner.GameBannerBuilder;
import dev.nincodedo.ninbot.components.stream.banner.GameBannerRepository;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class SteamGridDBBannerBuilder implements GameBannerBuilder {

    private SteamGridDBFeign steamGridDBFeign;
    private GameBannerRepository gameBannerRepository;
    private ExecutorService executorService;
    private Random random;

    public SteamGridDBBannerBuilder(SteamGridDBFeign steamGridDBFeign, GameBannerRepository gameBannerRepository) {
        this.steamGridDBFeign = steamGridDBFeign;
        this.gameBannerRepository = gameBannerRepository;
        this.executorService = Executors.newCachedThreadPool(new NamedThreadFactory("game-banner-builder"));
        this.random = new SecureRandom();
    }

    @Override
    public GameBannerRepository getGameBannerRepository() {
        return gameBannerRepository;
    }

    @Override
    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public GameBanner generateGameBannerFromTitle(String gameTitle) {
        var searchResponse = steamGridDBFeign.searchGameByName(gameTitle);
        if (searchResponse.isSuccess()) {
            var gameId = searchResponse.firstData().id();
            var logoResponse = steamGridDBFeign.retrieveLogoByGameId(gameId, new String[]{"official"});
            var heroResponse = steamGridDBFeign.retrieveHeroByGameId(gameId);
            if (logoResponse.isSuccess() && heroResponse.isSuccess() && !logoResponse.getData().isEmpty()
                    && !heroResponse.getData().isEmpty()) {
                var gameBanners = generateGameBanners(gameTitle, gameId, logoResponse.getData(),
                        heroResponse.getData());
                return randomBanner(gameBanners).orElse(null);
            }
        }
        return null;
    }

    private Optional<GameBanner> randomBanner(List<GameBanner> gameBanners) {
        return gameBanners.stream().min(StreamUtils.shuffle());
    }

    private List<GameBanner> generateGameBanners(String gameTitle, int gameId, List<GameImage> logos,
            List<GameImage> heroes) {
        var allBanners = gameBannerRepository.findAllByGameTitle(gameTitle);
        List<GameBanner> gameBanners = new ArrayList<>();
        log.trace("Generating banners for {}", gameTitle);
        for (int i = 0; i < 3; i++) {
            var logo = logos.get(random.nextInt(logos.size()));
            var hero = heroes.get(random.nextInt(heroes.size()));
            var gameBanner = allBanners.stream()
                    .filter(gameBanner1 -> gameBanner1.getLogoId() == logo.id()
                            && gameBanner1.getBackgroundId() == hero.id())
                    .findFirst()
                    .orElse(new GameBanner());
            gameBanner.setGameTitle(gameTitle);
            gameBanner.setGameId(gameId);
            gameBanner.setLogoId(logo.id());
            gameBanner.setBackgroundId(hero.id());
            var outputFileName = gameBanner.getFileName();
            var image = combineImages(hero.url(), logo.url(), outputFileName);
            gameBanner.setFile(image);
            gameBannerRepository.save(gameBanner);
            gameBanners.add(gameBanner);
        }
        log.debug("Finished with {} game banners generated", gameBanners.size());
        return gameBanners;
    }

    private File combineImages(String backgroundUrl, String logoUrl, String outputFileName) {
        int maxWidth = 750;
        BufferedImage background = readScaledImage(backgroundUrl, maxWidth);
        BufferedImage logo = readScaledImage(logoUrl, maxWidth / 2);

        if (background == null || logo == null) {
            return null;
        }
        int w = background.getWidth();
        int h = background.getHeight();
        double reductionMultiplier = 0.90;

        int logoHeight = logo.getHeight();
        int logoWidth = logo.getWidth();

        if (logoWidth > background.getWidth() / 2.0) {
            logoWidth = (int) (background.getWidth() / 2.0 * reductionMultiplier);
            logoHeight = (int) (logoHeight * ((double) logoWidth / logo.getWidth()));
        }
        if (logoHeight > background.getHeight() / 2.0) {
            logoHeight = (int) (background.getHeight() / 2.0 * reductionMultiplier);
            logoWidth = (int) (logoWidth * ((double) logoHeight / logo.getHeight()));
        }

        if (logoHeight != logo.getHeight() || logoWidth != logo.getWidth()) {
            var scaled = Scalr.resize(logo, logoWidth, logoHeight);
            logo.flush();
            logo = scaled;
        }

        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics g = combined.getGraphics();
        g.drawImage(background, 0, 0, null);
        int halfHeight = background.getHeight() / 2 - logo.getHeight() / 2;
        int widthBuffer = (int) (combined.getWidth() * 0.1);
        g.drawImage(logo, widthBuffer, halfHeight, null);
        File combinedFile = new File("cache", outputFileName);
        try {
            ImageIO.write(combined, "PNG", combinedFile);
        } catch (IOException e) {
            log.error("Failed to write image", e);
        }

        g.dispose();
        return combinedFile;
    }

    private BufferedImage readScaledImage(String url, int maxWidth) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new URL(url));
            if (image.getWidth() > maxWidth) {
                var scaled = Scalr.resize(image, Scalr.Mode.FIT_TO_WIDTH, maxWidth);
                image.flush();
                return scaled;
            }
        } catch (IOException e) {
            log.error("Failed to read image", e);
        }
        return image;
    }
}