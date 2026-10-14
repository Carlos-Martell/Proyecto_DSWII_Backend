package com.cibertec.proyectogrupo12.services.impl;

import com.cibertec.proyectogrupo12.entities.Giro;
import com.cibertec.proyectogrupo12.exceptions.BusinessException;
import com.cibertec.proyectogrupo12.exceptions.ResourceNotFoundException;
import com.cibertec.proyectogrupo12.models.GiroDTO;
import com.cibertec.proyectogrupo12.repositories.GiroRepository;
import com.cibertec.proyectogrupo12.services.GiroService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GiroServiceImpl implements GiroService {

    private final GiroRepository giroRepository;
    private final ObjectMapper objectMapper;

    @Override
    public GiroDTO crear(GiroDTO dto) {
        if (giroRepository.existsByNombre(dto.getNombre())) {
            throw new BusinessException("Ya existe un giro con el nombre indicado");
        }
        Giro giro = objectMapper.convertValue(dto, Giro.class);
        giro.setId(null);
        return toDTO(giroRepository.save(giro));
    }

    @Override
    public GiroDTO editar(Long id, GiroDTO dto) {
        Giro giro = obtenerEntidad(id);
        if (giroRepository.existsByNombreAndIdNot(dto.getNombre(), id)) {
            throw new BusinessException("Ya existe otro giro con el nombre indicado");
        }
        giro.setNombre(dto.getNombre());
        giro.setDescripcion(dto.getDescripcion());
        return toDTO(giroRepository.save(giro));
    }

    @Override
    public void eliminar(Long id) {
        Giro giro = obtenerEntidad(id);
        giroRepository.delete(giro);
    }

    @Override
    @Transactional(readOnly = true)
    public GiroDTO buscarPorId(Long id) {
        return toDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiroDTO> listar() {
        return giroRepository.findAll().stream().map(this::toDTO).toList();
    }

    private Giro obtenerEntidad(Long id) {
        return giroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Giro no encontrado: " + id));
    }

    private GiroDTO toDTO(Giro giro) {
        return objectMapper.convertValue(giro, GiroDTO.class);
    }
}
