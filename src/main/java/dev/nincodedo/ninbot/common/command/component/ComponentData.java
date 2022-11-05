package dev.nincodedo.ninbot.common.command.component;

import java.util.Arrays;
import java.util.List;

public record ComponentData(String name, String action, String data) {

    public boolean hasExtraData() {
        return data != null && data.contains(";") && data.split(";").length > 1;
    }

    @Override
    public String data() {
        return data.split(";")[0];
    }

    public List<String> extraData() {
        return Arrays.asList(data.split(";"));
    }
}
