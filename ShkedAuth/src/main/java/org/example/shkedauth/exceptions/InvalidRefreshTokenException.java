package org.example.shkedauth.exceptions;

public class InvalidRefreshTokenException extends RuntimeException{
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
