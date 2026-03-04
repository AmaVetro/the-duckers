package com.theduckers.backend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;




//security/JwtAuthenticationFilter:

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtService jwtService;
        private final CustomUserDetailsService userDetailsService;

        public JwtAuthenticationFilter(
                JwtService jwtService,
                CustomUserDetailsService userDetailsService
        ) {
                this.jwtService = jwtService;
                this.userDetailsService = userDetailsService;
        }

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {
                String path = request.getServletPath();

                return path.startsWith("/auth")
                        || path.startsWith("/swagger-ui")
                        || path.startsWith("/v3/api-docs")
                        || path.equals("/health");
        }

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
        ) throws ServletException, IOException {

                String authHeader = request.getHeader("Authorization");

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        filterChain.doFilter(request, response);
                        return;
                }

                String token = authHeader.substring(7);

                if (!jwtService.isTokenValid(token)) {
                        filterChain.doFilter(request, response);
                        return;
                }

                String email = jwtService.extractEmail(token);

                try {
                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                                UserDetailsImpl userDetails =
                                        (UserDetailsImpl) userDetailsService.loadUserByUsername(email);

                                UsernamePasswordAuthenticationToken authentication =
                                        UsernamePasswordAuthenticationToken.authenticated(
                                                userDetails,
                                                null,
                                                userDetails.getAuthorities()
                                        );

                                authentication.setDetails(
                                        new WebAuthenticationDetailsSource().buildDetails(request)
                                );

                                SecurityContextHolder.getContext().setAuthentication(authentication);
                        }

                } catch (Exception ex) {
                // Silent failure: request will proceed unauthenticated
                }

                filterChain.doFilter(request, response);
        }
}