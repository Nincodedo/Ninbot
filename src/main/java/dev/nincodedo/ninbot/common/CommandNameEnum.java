package dev.nincodedo.ninbot.common;

public interface CommandNameEnum {

    String name();

    default String get() {
        return this.name().toLowerCase();
    }
}
