package org.evpro.bookshopV4.exception;

import lombok.Getter;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;

@Getter
public class DatabaseException extends RuntimeException implements CustomException {
    private final ErrorResponse errorResponse;
    private final HttpStatusCode httpStatus;

    public DatabaseException(String message, String details, HttpStatusCode httpStatus) {
        super(message);
        this.errorResponse = new ErrorResponse("Database Error", details, System.currentTimeMillis());
        this.httpStatus = httpStatus;
    }
}