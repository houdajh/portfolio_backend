package com.devBrain.backend.service;

public class VectorStoreUnavailableException extends RuntimeException {

    public VectorStoreUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
