package org.evpro.bookshopV5.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.evpro.bookshopV5.data.response.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class SecurityExceptionHandlerConfig {
    public void handle(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        PrintWriter writer = response.getWriter();
        response.setStatus(errorResponse.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String error = objectMapper.writeValueAsString(errorResponse);
        writer.print(error);
        writer.flush();
    }
}
