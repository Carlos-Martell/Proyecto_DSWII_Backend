package com.cibertec.proyectogrupo12.services;

import com.cibertec.proyectogrupo12.models.SocioDTO;

import java.util.List;


public interface SocioService {

    SocioDTO crear(SocioDTO dto);

    SocioDTO editar(Long id, SocioDTO dto);

    void eliminar(Long id);

    SocioDTO buscarPorId(Long id);

    List<SocioDTO> listar();

    List<SocioDTO> listarPorEtapas(List<Integer> etapas);
}
