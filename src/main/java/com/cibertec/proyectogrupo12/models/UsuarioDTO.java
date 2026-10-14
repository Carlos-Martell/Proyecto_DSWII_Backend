package com.cibertec.proyectogrupo12.models;

import com.cibertec.proyectogrupo12.entities.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "El username es obligatorio")
    @Size(max = 50)
    private String username;

    @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100)
    private String apellidos;

    @Email(message = "El email no tiene un formato valido")
    @Size(max = 150)
    private String email;

    @NotNull(message = "El rol es obligatorio")
    private RolUsuario rol;

    private Boolean activo;
}
