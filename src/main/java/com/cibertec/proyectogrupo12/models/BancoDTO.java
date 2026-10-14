package com.cibertec.proyectogrupo12.models;

import com.cibertec.proyectogrupo12.entities.enums.Moneda;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BancoDTO {

    private Long id;

    @NotBlank(message = "El nombre del banco es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El numero de cuenta es obligatorio")
    @Size(max = 30)
    private String numeroCuenta;

    @Size(max = 30)
    private String cci;

    @NotNull(message = "La moneda es obligatoria")
    private Moneda moneda;
}
