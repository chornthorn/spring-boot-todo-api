package com.khodedev.app.common.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseWrapper<T> {
    private String message;
    private int statusCode;
    private String path;
    private T data;
}
