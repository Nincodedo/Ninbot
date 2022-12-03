package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseResponse<T> {
    private boolean success;
    private List<T> data;

    public T firstData() {
        return data.get(0);
    }
}
