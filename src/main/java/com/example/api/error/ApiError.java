package com.example.api.error;

import java.time.Instant;
import java.util.UUID;

@lombok.Data
@lombok.EqualsAndHashCode
@lombok.ToString
public class ApiError {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String id;

    public ApiError() {
        UUID uuid = UUID.randomUUID();
        this.id = uuid.toString();
    }

}
