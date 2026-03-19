package com.factory.factory_erp.config;

import com.factory.factory_erp.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        userEmail = jwtUtil.extractUsername(jwt);
        
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt)) {
                Claims claims = jwtUtil.extractAllClaims(jwt);

                Collection<? extends GrantedAuthority> authoritiesFromToken = extractAuthoritiesFromClaims(claims);
                Object principal = userEmail;

                if (authoritiesFromToken == null || authoritiesFromToken.isEmpty()) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    principal = userDetails;
                    authoritiesFromToken = userDetails.getAuthorities();
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        authoritiesFromToken
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private Collection<? extends GrantedAuthority> extractAuthoritiesFromClaims(Claims claims) {
        if (claims == null) {
            return List.of();
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof Collection<?>) {
            for (Object role : (Collection<?>) rolesObj) {
                if (role != null) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString()));
                }
            }
        }

        Object permsObj = claims.get("permissions");
        if (permsObj instanceof Collection<?>) {
            for (Object perm : (Collection<?>) permsObj) {
                if (perm != null) {
                    authorities.add(new SimpleGrantedAuthority("PERM_" + perm.toString()));
                }
            }
        }

        return authorities;
    }
}
