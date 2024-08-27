package org.evpro.bookshopV3.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private String details;
    private int statusCode;
    private long timestamp;

}