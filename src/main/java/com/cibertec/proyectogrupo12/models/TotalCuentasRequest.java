package com.cibertec.proyectogrupo12.models;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalCuentasRequest {

    @NotEmpty(message = "Debe seleccionar al menos una cuenta")
    private List<Long> cuentaIds;
}
