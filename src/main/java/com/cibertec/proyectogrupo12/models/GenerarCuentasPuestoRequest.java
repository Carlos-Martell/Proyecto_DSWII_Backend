package com.cibertec.proyectogrupo12.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerarCuentasPuestoRequest {

    @NotNull(message = "El servicio es obligatorio")
    private Long servicioId;

    @NotBlank(message = "El periodo es obligatorio")
    private String periodo;

    @NotEmpty(message = "Debe indicar al menos un puesto")
    private List<Long> puestoIds;
}
