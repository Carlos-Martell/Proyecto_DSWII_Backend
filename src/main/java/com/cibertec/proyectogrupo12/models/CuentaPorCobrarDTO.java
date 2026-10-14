package com.cibertec.proyectogrupo12.models;

import com.cibertec.proyectogrupo12.entities.enums.EstadoCuenta;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaPorCobrarDTO {

    private Long id;

    @NotNull(message = "El servicio es obligatorio")
    private Long servicioId;

    private Long socioId;

    private Long puestoId;

    @NotBlank(message = "El periodo es obligatorio")
    @Size(max = 20)
    private String periodo;

    @NotNull(message = "El monto es obligatorio")
    @PositiveOrZero(message = "El monto no puede ser negativo")
    private BigDecimal monto;

    private EstadoCuenta estado;

    private BigDecimal lecturaInicial;

    private BigDecimal lecturaFinal;

    private LocalDate fechaGeneracion;

    private LocalDate fechaPago;
}
