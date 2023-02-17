package dev.nincodedo.ninbot.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.function.Supplier;

@Configuration
public class GameBannerMetrics {

    @Bean
    public MeterBinder gameBanners() {
        return registry -> Gauge.builder("bot.stream.banners.cache.count", cacheCount())
                .description("The number of game banners currently in cache")
                .baseUnit("banners")
                .register(registry);
    }

    private Supplier<Number> cacheCount() {
        return () -> {
            var cacheList = new File("cache").listFiles();
            if (cacheList == null) {
                return -1;
            } else {
                return cacheList.length;
            }
        };
    }
}
