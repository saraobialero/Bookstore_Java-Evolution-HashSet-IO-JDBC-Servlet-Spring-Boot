package org.evpro.bookshopV4.exception;

import lombok.Getter;
import org.evpro.bookshopV4.model.UserHasBook;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;

@Getter
public class UserHasBookException extends RuntimeException implements CustomException {
    private final ErrorResponse errorResponse;
    private final HttpStatusCode httpStatus;

    public UserHasBookException(String message, HttpStatusCode httpStatus) {
        super(message);
        this.errorResponse = new ErrorResponse("Loan Error", System.currentTimeMillis());
        this.httpStatus = httpStatus;
    }
}