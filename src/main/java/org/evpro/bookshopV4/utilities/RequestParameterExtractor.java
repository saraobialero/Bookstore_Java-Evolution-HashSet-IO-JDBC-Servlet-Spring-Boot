package org.evpro.bookshopV4.utilities;

import javax.servlet.http.HttpServletRequest;
import org.evpro.bookshopV4.exception.BadRequestException;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import static org.evpro.bookshopV4.utilities.CodeMsg.MP_CODE;

public class RequestParameterExtractor {

    public static int extractIntParameter(HttpServletRequest request, String paramName) {
        String paramValue = request.getParameter(paramName);
        if (paramValue == null || paramValue.trim().isEmpty()) {
            throw new BadRequestException(
                    MP_CODE + paramName,
                    HttpStatusCode.BAD_REQUEST
            );
        }
        try {
            return Integer.parseInt(paramValue);
        } catch (NumberFormatException e) {
            throw new BadRequestException(
                    "Invalid integer format for parameter: " + paramName,
                    HttpStatusCode.BAD_REQUEST
            );
        }
    }

    public static String extractStringParameter(HttpServletRequest request, String paramName) {
        String paramValue = request.getParameter(paramName);
        if (paramValue == null || paramValue.trim().isEmpty()) {
            throw new BadRequestException(
                    MP_CODE + paramName,
                    HttpStatusCode.BAD_REQUEST
            );
        }
        return paramValue;
    }

    public static boolean extractBooleanParameter(HttpServletRequest request, String paramName) {
        String paramValue = request.getParameter(paramName);
        if (paramValue == null || paramValue.trim().isEmpty()) {
            throw new BadRequestException(
                    MP_CODE + paramName,
                    HttpStatusCode.BAD_REQUEST
            );
        }
        return Boolean.parseBoolean(paramValue);
    }

    public static <T extends Enum<T>> T extractEnumParameter(HttpServletRequest request, String paramName, Class<T> enumClass) {
        String paramValue = extractStringParameter(request, paramName);
        try {
            return Enum.valueOf(enumClass, paramValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid value for enum parameter: " + paramName + ". Allowed values are: " + String.join(", ", getEnumValues(enumClass)),
                    HttpStatusCode.BAD_REQUEST
            );
        }
    }

    public static LocalDate extractLocalDateParameter(HttpServletRequest request, String paramName) {
        String dateString = extractStringParameter(request, paramName);
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(
                    "Invalid date format for parameter: " + paramName,
                    HttpStatusCode.BAD_REQUEST
            );
        }
    }

    private static <T extends Enum<T>> String[] getEnumValues(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .toArray(String[]::new);
    }

}