package com.cibertec.proyectogrupo12.security;

import com.cibertec.proyectogrupo12.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.List;

import static com.cibertec.proyectogrupo12.security.Constants.*;

@Configuration
public class JWTAuthenticationConfig {

    public String getJWTToken(Usuario usuario) {
        List<String> authorities = List.of("ROLE_" + usuario.getRol().name());

        String token = Jwts.builder()
                .setId(String.valueOf(usuario.getId()))
                .setSubject(usuario.getUsername())
                .claim("nombre", usuario.getNombres())
                .claim("apellido", usuario.getApellidos())
                .claim("authorities", authorities)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_SECONDS))
                .signWith(getSigninKey(SECRET_TEXT), SignatureAlgorithm.HS256)
                .compact();

        return TOKEN_PREFIX + token;
    }

    public Claims decodeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey(SECRET_TEXT))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
