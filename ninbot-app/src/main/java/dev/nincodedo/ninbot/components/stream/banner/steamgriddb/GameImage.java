package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import java.util.List;

public record GameImage(int id, int score, String style, String url, String thumb, List<String> tags,
                        String language, boolean lock) {
}
