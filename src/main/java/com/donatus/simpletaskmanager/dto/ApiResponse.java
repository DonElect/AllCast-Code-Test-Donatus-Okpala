package com.donatus.simpletaskmanager.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ApiResponse<T> {
    private String code;
    private String description;
    private T responseData;

    public ApiResponse(String code, String description) {
        this.code = code;
        this.description = description;
        this.responseData = null;

    }
    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.description = message;
        this.responseData = data;
    }
}
