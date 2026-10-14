package com.cibertec.proyectogrupo12.services.impl;

import com.cibertec.proyectogrupo12.entities.Servicio;
import com.cibertec.proyectogrupo12.exceptions.BusinessException;
import com.cibertec.proyectogrupo12.exceptions.ResourceNotFoundException;
import com.cibertec.proyectogrupo12.models.ServicioDTO;
import com.cibertec.proyectogrupo12.repositories.ServicioRepository;
import com.cibertec.proyectogrupo12.services.ServicioService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final ObjectMapper objectMapper;

    @Override
    public ServicioDTO crear(ServicioDTO dto) {
        validarConsumo(dto);
        Servicio servicio = objectMapper.convertValue(dto, Servicio.class);
        servicio.setId(null);
        return toDTO(servicioRepository.save(servicio));
    }

    @Override
    public ServicioDTO editar(Long id, ServicioDTO dto) {
        validarConsumo(dto);
        Servicio servicio = obtenerEntidad(id);
        servicio.setNombre(dto.getNombre());
        servicio.setRecurrencia(dto.getRecurrencia());
        servicio.setCosto(dto.getCosto());
        servicio.setMoneda(dto.getMoneda());
        servicio.setCargoA(dto.getCargoA());
        servicio.setEsConsumo(dto.getEsConsumo());
        servicio.setCostoUnitario(dto.getCostoUnitario());
        return toDTO(servicioRepository.save(servicio));
    }

    @Override
    public void eliminar(Long id) {
        servicioRepository.delete(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioDTO buscarPorId(Long id) {
        return toDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioDTO> listar() {
        return servicioRepository.findAll().stream().map(this::toDTO).toList();
    }

    private void validarConsumo(ServicioDTO dto) {
        if (Boolean.TRUE.equals(dto.getEsConsumo()) && dto.getCostoUnitario() == null) {
            throw new BusinessException("Un servicio por consumo requiere costo unitario");
        }
    }

    private Servicio obtenerEntidad(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + id));
    }

    private ServicioDTO toDTO(Servicio servicio) {
        return objectMapper.convertValue(servicio, ServicioDTO.class);
    }
}
