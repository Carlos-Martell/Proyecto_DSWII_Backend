package com.cibertec.proyectogrupo12.services;

import com.cibertec.proyectogrupo12.models.EgresoCargaMasivaResultado;
import com.cibertec.proyectogrupo12.models.EgresoDTO;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;


public interface EgresoService {

    EgresoDTO registrar(EgresoDTO dto, Long usuarioId);

    EgresoCargaMasivaResultado registrarMasivo(InputStream contenido, String nombreArchivo, Long usuarioId);

    List<EgresoDTO> listarPorMes(LocalDate fechaDelMes);

    EgresoDTO buscarPorId(Long id);

    EgresoDTO anular(Long id);

    EgresoDTO procesar(Long id);
}
