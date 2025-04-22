package com.lkrs.portfoliobe.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${api.client.key}")
    private String clientKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String incomingKey = request.getHeader("X-API-KEY");
        if (!clientKey.equals(incomingKey)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid API key");
            return;
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("api-client",null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
