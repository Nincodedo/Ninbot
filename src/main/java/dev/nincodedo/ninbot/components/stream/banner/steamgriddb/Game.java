package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import java.util.List;

public record Game(List<String> types, int id, String name, boolean verified, int release_date) {

}
