package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.ninbot.components.stream.banner.steamgriddb.SteamGridDBBannerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SteamGridDBBannerBuilderTest {

    @Autowired
    SteamGridDBBannerBuilderTest(SteamGridDBBannerBuilder steamGridDBBannerBuilder){
        this.steamGridDBBannerBuilder = steamGridDBBannerBuilder;
    }

    @MockBean
    ShardManager shardManager;
    SteamGridDBBannerBuilder steamGridDBBannerBuilder;

    public static Stream<String> gameTitles() {
        return Stream.of(
                "Kirby Air Ride",
                "Stardew Valley",
                "Oxygen Not Included",
                "Tunic",
                "The Legend of Zelda: Breath of the Wild",
                "Super Mario 64",
                "Software and Game Development",
                "Minecraft",
                "Demon's Souls",
                "Kirby 64: The Crystal Shards",
                "Stray",
                "Warframe",
                "Elden Ring",
                "The Legend of Zelda: A Link to the Past",
                "Crypt of the Necrodancer",
                "Into the breach",
                "Paper Mario");
    }

    @BeforeAll
    public static void before() throws IOException {
        File cacheDirectory = new File("cache");
        if (cacheDirectory.exists()) {
            FileUtils.cleanDirectory(cacheDirectory);
        } else {
            cacheDirectory.mkdir();
        }
    }

    @ParameterizedTest
    @MethodSource("gameTitles")
    void getGameBannerFor(String gameTitle) {
        //var test = steamGridDBBannerBuilder.getGameBanner(gameTitle);
        //System.out.println(test);
    }
}
