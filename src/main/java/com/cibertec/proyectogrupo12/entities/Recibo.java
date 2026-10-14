package com.cibertec.proyectogrupo12.entities;

import com.cibertec.proyectogrupo12.entities.enums.TipoRecibo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "recibos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"cuenta", "banco", "usuario"})
public class Recibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String correlativo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoRecibo tipo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id")
    private CuentaPorCobrar cuenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banco_id")
    private Banco banco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(length = 150)
    private String depositante;

    @Column(length = 100)
    private String categoria;

    @Column(length = 255)
    private String concepto;

    @Column(name = "fecha_deposito")
    private LocalDate fechaDeposito;
}
