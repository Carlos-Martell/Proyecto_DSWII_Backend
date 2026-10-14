package com.cibertec.proyectogrupo12.entities;

import com.cibertec.proyectogrupo12.entities.enums.CargoA;
import com.cibertec.proyectogrupo12.entities.enums.Moneda;
import com.cibertec.proyectogrupo12.entities.enums.Recurrencia;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "servicios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Recurrencia recurrencia;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Moneda moneda;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo_a", nullable = false, length = 10)
    private CargoA cargoA;

    @Builder.Default
    @Column(name = "es_consumo", nullable = false)
    private Boolean esConsumo = Boolean.FALSE;

    @Column(name = "costo_unitario", precision = 12, scale = 4)
    private BigDecimal costoUnitario;
}
