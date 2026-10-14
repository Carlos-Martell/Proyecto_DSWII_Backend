package com.cibertec.proyectogrupo12.models;

import com.cibertec.proyectogrupo12.entities.enums.CargoA;
import com.cibertec.proyectogrupo12.entities.enums.Moneda;
import com.cibertec.proyectogrupo12.entities.enums.Recurrencia;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioDTO {

    private Long id;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotNull(message = "La recurrencia es obligatoria")
    private Recurrencia recurrencia;

    @NotNull(message = "El costo es obligatorio")
    @PositiveOrZero(message = "El costo no puede ser negativo")
    private BigDecimal costo;

    @NotNull(message = "La moneda es obligatoria")
    private Moneda moneda;

    @NotNull(message = "Debe indicar a quien se carga el servicio")
    private CargoA cargoA;

    @NotNull(message = "Debe indicar si el servicio depende de consumo")
    private Boolean esConsumo;

    @PositiveOrZero(message = "El costo unitario no puede ser negativo")
    private BigDecimal costoUnitario;
}
