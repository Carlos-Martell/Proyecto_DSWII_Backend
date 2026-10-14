package com.cibertec.proyectogrupo12.services;

import com.cibertec.proyectogrupo12.models.BancoDTO;

import java.util.List;


public interface BancoService {

    BancoDTO crear(BancoDTO dto);

    BancoDTO editar(Long id, BancoDTO dto);

    void eliminar(Long id);

    BancoDTO buscarPorId(Long id);

    List<BancoDTO> listar();
}
