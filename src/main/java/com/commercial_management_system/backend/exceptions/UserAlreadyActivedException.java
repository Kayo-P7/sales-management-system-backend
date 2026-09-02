package com.commercial_management_system.backend.exceptions;

public class UserAlreadyActivedException extends RuntimeException {
    public UserAlreadyActivedException(String message) {
        super(message);
    }

    public UserAlreadyActivedException(){
        super("User already actived");
    }
}
