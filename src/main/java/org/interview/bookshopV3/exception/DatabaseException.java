package org.interview.bookshopV3.exception;

import lombok.Getter;

@Getter
public class DatabaseException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public DatabaseException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }
}

