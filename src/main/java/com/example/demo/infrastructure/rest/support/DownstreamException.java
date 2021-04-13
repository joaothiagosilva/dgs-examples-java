package com.example.demo.infrastructure.rest.support;

public class DownstreamException extends RuntimeException {
    private final int status;

    public DownstreamException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
