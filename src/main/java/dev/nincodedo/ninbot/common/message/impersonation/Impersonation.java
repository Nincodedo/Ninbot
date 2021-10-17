package dev.nincodedo.ninbot.common.message.impersonation;

public record Impersonation(String name, String iconUrl) {
    public static Impersonation of(String name, String iconUrl) {
        return new Impersonation(name, iconUrl);
    }
}
