package com.cibertec.proyectogrupo12.models;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EgresoCargaMasivaResultado {

    private int totalFilas;
    private int registrados;
    private List<String> errores;
}
