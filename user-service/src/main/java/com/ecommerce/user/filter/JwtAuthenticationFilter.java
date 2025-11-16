package com.ecommerce.user.filter;

import com.ecommerce.user.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();
        
        // Skip filter for public endpoints - let them through without authentication
        if (requestURI.startsWith("/users/register") || 
            requestURI.startsWith("/users/login") || 
            requestURI.startsWith("/actuator") || 
            requestURI.startsWith("/swagger-ui") || 
            requestURI.startsWith("/v3/api-docs") ||
            requestURI.startsWith("/diagnostics")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // For protected endpoints, validate JWT token
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                // Extract username - this will also validate the token signature
                username = jwtUtil.extractUsername(jwt);
                
                // If we got here, token signature is valid. Now check expiration
                if (username != null && !jwtUtil.isTokenExpired(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("JWT token validated successfully for user: " + username + " on " + requestURI);
                } else {
                    logger.warn("JWT token validation failed: username=" + username + ", expired=" + (username != null ? jwtUtil.isTokenExpired(jwt) : "N/A") + " for " + requestURI);
                    // Clear any existing authentication if token is invalid
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                logger.error("JWT token validation failed for " + requestURI + ": " + e.getMessage());
                // Clear any existing authentication if token is invalid
                SecurityContextHolder.clearContext();
            }
        } else {
            // No auth header for protected endpoint - Spring Security will handle the 403
            logger.debug("No Authorization header found for protected endpoint: " + requestURI);
        }

        filterChain.doFilter(request, response);
    }
}

