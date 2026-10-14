package com.cibertec.proyectogrupo12.models;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CanjeBancoRequest {

    @NotNull(message = "La cuenta es obligatoria")
    private Long cuentaId;

    @NotNull(message = "El banco es obligatorio")
    private Long bancoId;

    @NotNull(message = "La fecha de deposito es obligatoria")
    private LocalDate fechaDeposito;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;
}
