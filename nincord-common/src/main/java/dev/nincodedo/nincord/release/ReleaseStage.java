package dev.nincodedo.nincord.release;

public interface ReleaseStage {
    default ReleaseType getReleaseType() {
        return ReleaseType.PUBLIC;
    }
}
