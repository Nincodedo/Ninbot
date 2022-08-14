package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import dev.nincodedo.ninbot.common.StreamUtils;
import dev.nincodedo.ninbot.components.stream.banner.GameBanner;
import dev.nincodedo.ninbot.components.stream.banner.GameBannerBuilder;
import dev.nincodedo.ninbot.components.stream.banner.GameBannerRepository;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class SteamGridDBBannerBuilder implements GameBannerBuilder {

    private SteamGridDBFeign steamGridDBFeign;
    private GameBannerRepository gameBannerRepository;
    private Random random;

    public SteamGridDBBannerBuilder(SteamGridDBFeign steamGridDBFeign, GameBannerRepository gameBannerRepository) {
        this.steamGridDBFeign = steamGridDBFeign;
        this.gameBannerRepository = gameBannerRepository;
        this.random = new SecureRandom();
    }

    @Override
    public GameBannerRepository getGameBannerRepository() {
        return gameBannerRepository;
    }

    @Override
    public Optional<GameBanner> generateGameBannerFromTitle(String gameTitle) {
        var searchResponse = steamGridDBFeign.searchGameByName(gameTitle);
        if (searchResponse.isSuccess()) {
            var gameId = searchResponse.firstData().id();
            var logoResponse = steamGridDBFeign.retrieveLogoByGameId(gameId, new String[]{"official"});
            var heroResponse = steamGridDBFeign.retrieveHeroByGameId(gameId);
            if (logoResponse.isSuccess() && heroResponse.isSuccess() && !logoResponse.getData().isEmpty()
                    && !heroResponse.getData().isEmpty()) {
                var gameBanners = generateGameBanners(gameTitle, gameId, logoResponse.getData(),
                        heroResponse.getData());
                return randomBanner(gameBanners);
            }
        }
        return Optional.empty();
    }

    private Optional<GameBanner> randomBanner(List<GameBanner> gameBanners) {
        return gameBanners.stream().min(StreamUtils.shuffle());
    }

    private List<GameBanner> generateGameBanners(String gameTitle, int gameId, List<GameImage> logos,
            List<GameImage> heroes) {
        var allBanners = gameBannerRepository.findAllByGameTitle(gameTitle);
        List<GameBanner> gameBanners = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            var logo = logos.get(random.nextInt(logos.size()));
            var hero = heroes.get(random.nextInt(heroes.size()));
            if (isLogoHeroComboNotBad(gameTitle, logo, hero)) {
                var gameBanner = allBanners.stream()
                        .filter(gameBanner1 -> gameBanner1.getLogoId() == logo.id()
                                && gameBanner1.getBackgroundId() == hero.id()).findFirst().orElse(new GameBanner());
                gameBanner.setGameTitle(gameTitle);
                gameBanner.setGameId(gameId);
                gameBanner.setLogoId(logo.id());
                gameBanner.setBackgroundId(hero.id());
                var outputFileName = gameBanner.getFileName();
                var image = combineImages(hero.url(), logo.url(), outputFileName);
                gameBanner.setFile(image);
                gameBanners.add(gameBanner);
            }
        }
        log.debug("Finished with {} game banners generated", gameBanners.size());
        return gameBanners;
    }

    private boolean isLogoHeroComboNotBad(String gameTitle, GameImage logo, GameImage hero) {
        return getBadBanners(gameTitle).stream()
                .noneMatch(gameBanner -> gameBanner.getLogoId() == logo.id()
                        && gameBanner.getBackgroundId() == hero.id());
    }

    private File combineImages(String backgroundUrl, String logoUrl, String outputFileName) {
        int maxWidth = 750;
        BufferedImage background = getImageScaled(backgroundUrl, maxWidth);
        BufferedImage logo = getImageScaled(logoUrl, maxWidth / 2);

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
            var scaled = logo.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
            var output = new BufferedImage(logoWidth, logoHeight, BufferedImage.TYPE_INT_ARGB);
            output.getGraphics().drawImage(scaled, 0, 0, null);
            logo = output;
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
            log.error("Ooppsie poopsie");
        }

        g.dispose();
        return combinedFile;
    }

    private BufferedImage getImageScaled(String url, int maxWidth) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new URL(url));
            if (image.getWidth() > maxWidth) {
                int newHeight = (int) (image.getHeight() * ((double) maxWidth / image.getWidth()));
                var scaled = image.getScaledInstance(maxWidth, newHeight, Image.SCALE_SMOOTH);
                var output = new BufferedImage(maxWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                output.getGraphics().drawImage(scaled, 0, 0, null);
                image = output;
            }
        } catch (IOException e) {
            log.error("Ooppsie poopsie");
        }
        return image;
    }
}
