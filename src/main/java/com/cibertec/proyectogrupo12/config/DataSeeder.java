package com.cibertec.proyectogrupo12.config;

import com.cibertec.proyectogrupo12.entities.Usuario;
import com.cibertec.proyectogrupo12.entities.enums.RolUsuario;
import com.cibertec.proyectogrupo12.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private static final String USERNAME_ADMIN = "admin";
    private static final String PASSWORD_ADMIN = "Admin12345";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner crearUsuarioAdministradorInicial() {
        return args -> {
            if (usuarioRepository.count() == 0) {
                Usuario admin = Usuario.builder()
                        .username(USERNAME_ADMIN)
                        .password(passwordEncoder.encode(PASSWORD_ADMIN))
                        .nombres("Administrador")
                        .apellidos("Sistema")
                        .rol(RolUsuario.ADMINISTRADOR)
                        .activo(Boolean.TRUE)
                        .build();
                usuarioRepository.save(admin);
                log.warn("Se creo el usuario administrador inicial '{}' con contrasena por defecto. "
                        + "Cambiarla o crear otro usuario y eliminar este cuanto antes.", USERNAME_ADMIN);
            }
        };
    }
}
