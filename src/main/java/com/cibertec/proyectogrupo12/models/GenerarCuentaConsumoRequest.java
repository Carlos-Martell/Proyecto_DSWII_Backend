package com.cibertec.proyectogrupo12.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerarCuentaConsumoRequest {

    @NotNull(message = "El servicio es obligatorio")
    private Long servicioId;

    @NotBlank(message = "El periodo es obligatorio")
    private String periodo;

    @NotNull(message = "El puesto es obligatorio")
    private Long puestoId;

    @NotNull(message = "La lectura inicial es obligatoria")
    private BigDecimal lecturaInicial;

    @NotNull(message = "La lectura final es obligatoria")
    private BigDecimal lecturaFinal;
}
