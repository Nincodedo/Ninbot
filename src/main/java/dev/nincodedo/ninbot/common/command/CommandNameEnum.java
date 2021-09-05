package dev.nincodedo.ninbot.common.command;

public interface CommandNameEnum {

    String name();

    default String get() {
        return this.name().toLowerCase();
    }
}
