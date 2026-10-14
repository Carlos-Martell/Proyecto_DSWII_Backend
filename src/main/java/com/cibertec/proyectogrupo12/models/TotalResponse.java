package com.cibertec.proyectogrupo12.models;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalResponse {

    private BigDecimal total;
}
