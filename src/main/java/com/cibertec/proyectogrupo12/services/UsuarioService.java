package com.cibertec.proyectogrupo12.services;

import com.cibertec.proyectogrupo12.entities.Usuario;
import com.cibertec.proyectogrupo12.models.UsuarioDTO;

import java.util.List;


public interface UsuarioService {

    UsuarioDTO crear(UsuarioDTO dto);

    UsuarioDTO editar(Long id, UsuarioDTO dto);

    void eliminar(Long id);

    UsuarioDTO buscarPorId(Long id);

    List<UsuarioDTO> listar();

      Usuario validarCredenciales(String username, String password);
}
