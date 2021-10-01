package dev.nincodedo.ninbot.components.reaction;

import lombok.Getter;

enum ReactionMatchType {
    EXACT("exact"), CONTAINS("contains"), REGEX("regex");

    @Getter
    private final String name;

    ReactionMatchType(String name) {
        this.name = name;
    }
}
