package com.cibertec.proyectogrupo12.entities;

import com.cibertec.proyectogrupo12.entities.enums.Moneda;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "bancos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "numero_cuenta", nullable = false, length = 30)
    private String numeroCuenta;

    @Column(name = "cci", length = 30)
    private String cci;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Moneda moneda;
}
