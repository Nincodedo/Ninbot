package dev.nincodedo.ninbot.components.common;

public interface CommandNameEnum {

    String name();

    default String get() {
        return this.name().toLowerCase();
    }
}
