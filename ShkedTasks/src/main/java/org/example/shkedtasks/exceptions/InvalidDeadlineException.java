package org.example.shkedtasks.exceptions;

public class InvalidDeadlineException extends RuntimeException {
    public InvalidDeadlineException(String message) {
        super(message);
    }
}
