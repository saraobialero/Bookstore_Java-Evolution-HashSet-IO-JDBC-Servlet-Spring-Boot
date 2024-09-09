package org.evpro.bookshopV5.exception;

import com.project.response.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class AuthException extends RuntimeException {
    private ErrorResponse response;

    @Override
    public String getMessage() {
        return response.getMessage();
    }

}
