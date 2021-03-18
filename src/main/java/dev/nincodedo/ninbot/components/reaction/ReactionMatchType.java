package dev.nincodedo.ninbot.components.reaction;

public enum ReactionMatchType {
    EXACT("exact"), CONTAINS("contains"), REGEX("regex");
    private final String name;

    ReactionMatchType(String name) {
        this.name = name;
    }


    public String getName() {
        return this.name;
    }
}
