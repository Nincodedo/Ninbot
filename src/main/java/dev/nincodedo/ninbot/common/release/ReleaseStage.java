package dev.nincodedo.ninbot.common.release;

public interface ReleaseStage {
    default ReleaseType getReleaseType() {
        return ReleaseType.PUBLIC;
    }
}
