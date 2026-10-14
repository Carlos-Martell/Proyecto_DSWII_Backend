package com.cibertec.proyectogrupo12.models;

import com.cibertec.proyectogrupo12.entities.enums.RolUsuario;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String token;
    private String username;
    private String nombres;
    private String apellidos;
    private RolUsuario rol;
}
