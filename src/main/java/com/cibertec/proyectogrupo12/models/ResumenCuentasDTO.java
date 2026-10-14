package com.cibertec.proyectogrupo12.models;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenCuentasDTO {

    private List<CuentaPorCobrarDTO> cuentas;
    private List<ReciboDTO> movimientos;
}
