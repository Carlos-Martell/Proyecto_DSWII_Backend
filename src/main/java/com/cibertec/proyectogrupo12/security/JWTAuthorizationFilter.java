package com.cibertec.proyectogrupo12.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.cibertec.proyectogrupo12.security.Constants.*;


@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private Claims obtenerClaims(HttpServletRequest request) {
        String jwtToken = request.getHeader(HEADER_AUTHORIZATION).replace(TOKEN_PREFIX, "");
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey(SECRET_TEXT))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    @SuppressWarnings("unchecked")
    private void setAutenticacion(Claims claims) {
        List<String> authorities = claims.get("authorities", List.class);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                claims.getSubject(), null,
                authorities.stream().map(SimpleGrantedAuthority::new).toList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private boolean isJwtValid(HttpServletRequest request) {
        String authHeader = request.getHeader(HEADER_AUTHORIZATION);
        return authHeader != null && authHeader.startsWith(TOKEN_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (isJwtValid(request)) {
                Claims claims = obtenerClaims(request);
                if (claims.get("authorities") != null) {
                    setAutenticacion(claims);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }
}
