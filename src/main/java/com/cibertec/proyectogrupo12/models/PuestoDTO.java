package com.cibertec.proyectogrupo12.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuestoDTO {

    private Long id;

    @NotBlank(message = "El numero de puesto es obligatorio")
    @Size(max = 20)
    private String numero;

    private Long socioId;

    @NotNull(message = "El giro es obligatorio")
    private Long giroId;

    @Size(max = 150)
    private String inquilinoNombre;

    @Size(max = 20)
    private String inquilinoDocumento;

    private LocalDate fechaInicioVigencia;

    private LocalDate fechaFinVigencia;
}
