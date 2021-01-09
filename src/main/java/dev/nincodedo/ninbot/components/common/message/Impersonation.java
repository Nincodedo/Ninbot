package dev.nincodedo.ninbot.components.common.message;

public record Impersonation(String name, String iconUrl) {
    public static Impersonation of(String name, String iconUrl) {
        return new Impersonation(name, iconUrl);
    }
}
