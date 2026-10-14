package com.cibertec.proyectogrupo12.services.impl;

import com.cibertec.proyectogrupo12.entities.Usuario;
import com.cibertec.proyectogrupo12.exceptions.BusinessException;
import com.cibertec.proyectogrupo12.exceptions.ResourceNotFoundException;
import com.cibertec.proyectogrupo12.models.UsuarioDTO;
import com.cibertec.proyectogrupo12.repositories.UsuarioRepository;
import com.cibertec.proyectogrupo12.services.UsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Override
    public UsuarioDTO crear(UsuarioDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("Ya existe un usuario con ese username");
        }
        Usuario usuario = objectMapper.convertValue(dto, Usuario.class);
        usuario.setId(null);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (usuario.getActivo() == null) {
            usuario.setActivo(Boolean.TRUE);
        }
        return toDTO(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioDTO editar(Long id, UsuarioDTO dto) {
        Usuario usuario = obtenerEntidad(id);
        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol());
        if (dto.getActivo() != null) {
            usuario.setActivo(dto.getActivo());
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return toDTO(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.delete(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        return toDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listar() {
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario validarCredenciales(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario o contrasena invalidos"));
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BusinessException("El usuario se encuentra inactivo");
        }
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new BusinessException("Usuario o contrasena invalidos");
        }
        return usuario;
    }

    private Usuario obtenerEntidad(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        UsuarioDTO dto = objectMapper.convertValue(usuario, UsuarioDTO.class);
        dto.setPassword(null);
        return dto;
    }
}
