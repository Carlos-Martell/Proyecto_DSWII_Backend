package com.cibertec.proyectogrupo12.services.impl;

import com.cibertec.proyectogrupo12.entities.Giro;
import com.cibertec.proyectogrupo12.entities.Puesto;
import com.cibertec.proyectogrupo12.entities.Socio;
import com.cibertec.proyectogrupo12.exceptions.BusinessException;
import com.cibertec.proyectogrupo12.exceptions.ResourceNotFoundException;
import com.cibertec.proyectogrupo12.models.PuestoDTO;
import com.cibertec.proyectogrupo12.repositories.GiroRepository;
import com.cibertec.proyectogrupo12.repositories.PuestoRepository;
import com.cibertec.proyectogrupo12.repositories.SocioRepository;
import com.cibertec.proyectogrupo12.services.PuestoService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class PuestoServiceImpl implements PuestoService {

    private final PuestoRepository puestoRepository;
    private final SocioRepository socioRepository;
    private final GiroRepository giroRepository;
    private final ObjectMapper objectMapper;

    @Override
    public PuestoDTO crear(PuestoDTO dto) {
        if (puestoRepository.existsByNumero(dto.getNumero())) {
            throw new BusinessException("Ya existe un puesto con el numero indicado");
        }
        Puesto puesto = mapearCamposPlanos(dto, new Puesto());
        puesto.setGiro(obtenerGiro(dto.getGiroId()));
        puesto.setSocio(dto.getSocioId() != null ? obtenerSocio(dto.getSocioId()) : null);
        return toDTO(puestoRepository.save(puesto));
    }

    @Override
    public PuestoDTO editar(Long id, PuestoDTO dto) {
        Puesto puesto = obtenerEntidad(id);
        if (puestoRepository.existsByNumeroAndIdNot(dto.getNumero(), id)) {
            throw new BusinessException("Ya existe otro puesto con el numero indicado");
        }
        mapearCamposPlanos(dto, puesto);
        puesto.setGiro(obtenerGiro(dto.getGiroId()));
        puesto.setSocio(dto.getSocioId() != null ? obtenerSocio(dto.getSocioId()) : null);
        return toDTO(puestoRepository.save(puesto));
    }

    @Override
    public void eliminar(Long id) {
        puestoRepository.delete(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PuestoDTO buscarPorId(Long id) {
        return toDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PuestoDTO> listar() {
        return puestoRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PuestoDTO> listarPorSocio(Long socioId) {
        return puestoRepository.findBySocioId(socioId).stream().map(this::toDTO).toList();
    }

    private Puesto mapearCamposPlanos(PuestoDTO dto, Puesto puesto) {
        Long idOriginal = puesto.getId();
        try {
            objectMapper.updateValue(puesto, dto);
        } catch (Exception e) {
            throw new BusinessException("No se pudo mapear los datos del puesto: " + e.getMessage());
        }
        puesto.setId(idOriginal);
        return puesto;
    }

    private Giro obtenerGiro(Long giroId) {
        return giroRepository.findById(giroId)
                .orElseThrow(() -> new ResourceNotFoundException("Giro no encontrado: " + giroId));
    }

    private Socio obtenerSocio(Long socioId) {
        return socioRepository.findById(socioId)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado: " + socioId));
    }

    private Puesto obtenerEntidad(Long id) {
        return puestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Puesto no encontrado: " + id));
    }

    private PuestoDTO toDTO(Puesto puesto) {
        PuestoDTO dto = objectMapper.convertValue(puesto, PuestoDTO.class);
        dto.setGiroId(puesto.getGiro() != null ? puesto.getGiro().getId() : null);
        dto.setSocioId(puesto.getSocio() != null ? puesto.getSocio().getId() : null);
        return dto;
    }
}
