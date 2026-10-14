package com.cibertec.proyectogrupo12.models;

import com.cibertec.proyectogrupo12.entities.enums.TipoRecibo;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReciboDTO {

    private Long id;

    private String correlativo;

    @NotNull(message = "El tipo de recibo es obligatorio")
    private TipoRecibo tipo;

    private LocalDateTime fecha;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    private Long cuentaId;

    private Long bancoId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @Size(max = 150)
    private String depositante;

    @Size(max = 100)
    private String categoria;

    @Size(max = 255)
    private String concepto;

    private LocalDate fechaDeposito;
}
