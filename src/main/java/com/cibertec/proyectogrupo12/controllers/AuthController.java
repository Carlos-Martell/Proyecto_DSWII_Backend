package com.cibertec.proyectogrupo12.controllers;

import com.cibertec.proyectogrupo12.entities.Usuario;
import com.cibertec.proyectogrupo12.entities.enums.RolUsuario;
import com.cibertec.proyectogrupo12.models.LoginRequest;
import com.cibertec.proyectogrupo12.models.LoginResponse;
import com.cibertec.proyectogrupo12.security.JWTAuthenticationConfig;
import com.cibertec.proyectogrupo12.services.UsuarioService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cibertec.proyectogrupo12.security.Constants.HEADER_AUTHORIZATION;
import static com.cibertec.proyectogrupo12.security.Constants.TOKEN_PREFIX;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JWTAuthenticationConfig jwtAuthenticationConfig;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.validarCredenciales(request.getUsername(), request.getPassword());
        String token = jwtAuthenticationConfig.getJWTToken(usuario);

        LoginResponse response = LoginResponse.builder()
                .id(usuario.getId())
                .token(token)
                .username(usuario.getUsername())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .rol(usuario.getRol())
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> obtenerUsuarioActual(HttpServletRequest request) {
        String jwtToken = request.getHeader(HEADER_AUTHORIZATION).replace(TOKEN_PREFIX, "");
        Claims claims = jwtAuthenticationConfig.decodeToken(jwtToken);

        @SuppressWarnings("unchecked")
        List<String> authorities = claims.get("authorities", List.class);
        String rolStr = authorities.get(0).replace("ROLE_", "");

        LoginResponse response = LoginResponse.builder()
                .id(Long.valueOf(claims.getId()))
                .username(claims.getSubject())
                .nombres(claims.get("nombre", String.class))
                .apellidos(claims.get("apellido", String.class))
                .rol(RolUsuario.valueOf(rolStr))
                .build();
        return ResponseEntity.ok(response);
}
}
