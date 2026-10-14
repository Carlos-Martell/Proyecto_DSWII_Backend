package com.cibertec.proyectogrupo12.services;

import com.cibertec.proyectogrupo12.models.ServicioDTO;

import java.util.List;


public interface ServicioService {

    ServicioDTO crear(ServicioDTO dto);

    ServicioDTO editar(Long id, ServicioDTO dto);

    void eliminar(Long id);

    ServicioDTO buscarPorId(Long id);

    List<ServicioDTO> listar();
}
