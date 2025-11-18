package com.vertra.adapters.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class DelegatingAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        log.debug("Access denied: {}", accessDeniedException.getMessage());

        // Store the exception as a request attribute
        request.setAttribute("javax.servlet.error.exception", accessDeniedException);

        response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
    }
}
