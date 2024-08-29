package org.evpro.bookshopV4.exception;

import lombok.Getter;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;

@Getter
public class UserException extends RuntimeException implements CustomException {
    private final ErrorResponse errorResponse;
    private final HttpStatusCode httpStatus;

    public UserException(String message, HttpStatusCode httpStatus) {
        super(message);
        this.errorResponse = new ErrorResponse("User Error", System.currentTimeMillis());
        this.httpStatus = httpStatus;
    }
}