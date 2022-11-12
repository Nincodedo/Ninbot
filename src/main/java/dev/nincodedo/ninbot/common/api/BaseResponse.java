package dev.nincodedo.ninbot.common.api;

import lombok.Data;

import java.util.List;

@Data
public class BaseResponse<T> {

    protected List<T> data;
    protected int count;

    public BaseResponse(List<T> data) {
        this.data = data;
        this.count = data.size();
    }
}
