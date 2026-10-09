package com.commercial_management_system.backend.exception;

public class UserAlreadyDeactivedException extends RuntimeException {

    public UserAlreadyDeactivedException(String message) {
        super(message);
    }

    public UserAlreadyDeactivedException() {
        super("usuario ja se encontra desativado");
    }
}
