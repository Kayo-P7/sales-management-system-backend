package com.commercial_management_system.backend.exceptions;

public class UserAlreadyDeactivedException extends RuntimeException {
    public UserAlreadyDeactivedException(String message) {
        super(message);
    }
    public UserAlreadyDeactivedException(){
        super("User already deactived");
    }
}
