package com.commercial_management_system.backend.exception;

public class UserAlreadyActivedException extends RuntimeException {

    public UserAlreadyActivedException(String message) {
        super(message);
    }

    public UserAlreadyActivedException() {
        super("usuario ja se encontra ativo");
    }
}
