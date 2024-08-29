package org.evpro.bookshopV4.filter;

import org.evpro.bookshopV4.exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class ExceptionHandlerFilter implements Filter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof CustomException) {
                CustomException ce = (CustomException) e;
                handleException(response, ce.getErrorResponse(), ce.getHttpStatus());
            } else {
                ErrorResponse errorResponse = new ErrorResponse(
                        "Internal Server Error",
                        e.getMessage(),
                        System.currentTimeMillis()
                );
                handleException(response, errorResponse, HttpStatusCode.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private void handleException(ServletResponse response, ErrorResponse errorResponse, HttpStatusCode status)
            throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(status.getCode());
        httpResponse.setContentType("application/json");
        httpResponse.getWriter().write(convertToJson(errorResponse));
    }

    private String convertToJson(ErrorResponse errorResponse) throws IOException {
        return objectMapper.writeValueAsString(errorResponse);
    }
}