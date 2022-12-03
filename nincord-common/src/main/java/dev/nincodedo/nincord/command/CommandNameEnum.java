package dev.nincodedo.nincord.command;

public interface CommandNameEnum {

    String name();

    /**
     * Returns the lowercase name of the enum.
     *
     * @return lowercase String
     */
    default String get() {
        return this.name().toLowerCase();
    }
}
