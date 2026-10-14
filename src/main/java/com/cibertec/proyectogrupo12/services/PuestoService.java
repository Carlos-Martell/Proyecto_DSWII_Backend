package com.cibertec.proyectogrupo12.services;

import com.cibertec.proyectogrupo12.models.PuestoDTO;

import java.util.List;


public interface PuestoService {

    PuestoDTO crear(PuestoDTO dto);

    PuestoDTO editar(Long id, PuestoDTO dto);

    void eliminar(Long id);

    PuestoDTO buscarPorId(Long id);

    List<PuestoDTO> listar();

    List<PuestoDTO> listarPorSocio(Long socioId);
}
