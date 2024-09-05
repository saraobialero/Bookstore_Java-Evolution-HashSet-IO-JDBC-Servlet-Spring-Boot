package org.evpro.bookshopV4.exception;

import lombok.Getter;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;

@Getter
public class BadRequestException extends RuntimeException implements CustomException{
    private final ErrorResponse errorResponse;
    private final HttpStatusCode httpStatus;

    public BadRequestException(String message, HttpStatusCode httpStatus) {
        super(message);
        this.errorResponse = new ErrorResponse("Bad request ", System.currentTimeMillis());
        this.httpStatus = httpStatus;
    }

}
