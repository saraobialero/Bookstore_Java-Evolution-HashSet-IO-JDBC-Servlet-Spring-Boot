package org.evpro.bookshopV4.exception;

import org.evpro.bookshopV4.model.enums.HttpStatusCode;

public interface CustomException {
    ErrorResponse getErrorResponse();
    HttpStatusCode getHttpStatus();
}