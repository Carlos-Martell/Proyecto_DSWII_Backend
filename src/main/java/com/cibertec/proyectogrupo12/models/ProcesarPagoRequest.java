package com.cibertec.proyectogrupo12.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcesarPagoRequest {

    @NotEmpty(message = "Debe seleccionar al menos una cuenta")
    private List<Long> cuentaIds;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;
}
