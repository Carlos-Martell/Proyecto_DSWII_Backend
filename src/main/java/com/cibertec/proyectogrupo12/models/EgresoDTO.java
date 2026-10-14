package com.cibertec.proyectogrupo12.models;

import com.cibertec.proyectogrupo12.entities.enums.EstadoEgreso;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EgresoDTO {

    private Long id;

    @NotBlank(message = "El numero de documento es obligatorio")
    @Size(max = 30)
    private String numeroDocumento;

    @NotBlank(message = "El proveedor es obligatorio")
    @Size(max = 150)
    private String proveedor;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El importe es obligatorio")
    @Positive(message = "El importe debe ser mayor a cero")
    private BigDecimal importe;

    @Size(max = 255)
    private String documentoAsociado;

    @Size(max = 255)
    private String motivo;

    private EstadoEgreso estado;

    private Long usuarioId;

    private String comprobanteUrl;
}
