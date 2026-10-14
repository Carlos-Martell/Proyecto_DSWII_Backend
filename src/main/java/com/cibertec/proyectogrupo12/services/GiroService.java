package com.cibertec.proyectogrupo12.services;

import com.cibertec.proyectogrupo12.models.GiroDTO;

import java.util.List;


public interface GiroService {

    GiroDTO crear(GiroDTO dto);

    GiroDTO editar(Long id, GiroDTO dto);

    void eliminar(Long id);

    GiroDTO buscarPorId(Long id);

    List<GiroDTO> listar();
}
