package com.cibertec.proyectogrupo12.services.impl;

import com.cibertec.proyectogrupo12.entities.Socio;
import com.cibertec.proyectogrupo12.exceptions.BusinessException;
import com.cibertec.proyectogrupo12.exceptions.ResourceNotFoundException;
import com.cibertec.proyectogrupo12.models.SocioDTO;
import com.cibertec.proyectogrupo12.repositories.SocioRepository;
import com.cibertec.proyectogrupo12.services.SocioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class SocioServiceImpl implements SocioService {

    private final SocioRepository socioRepository;
    private final ObjectMapper objectMapper;

    @Override
    public SocioDTO crear(SocioDTO dto) {
        if (socioRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessException("Ya existe un socio con el codigo indicado");
        }
        Socio socio = objectMapper.convertValue(dto, Socio.class);
        socio.setId(null);
        return toDTO(socioRepository.save(socio));
    }

    @Override
    public SocioDTO editar(Long id, SocioDTO dto) {
        Socio socio = obtenerEntidad(id);
        if (socioRepository.existsByCodigoAndIdNot(dto.getCodigo(), id)) {
            throw new BusinessException("Ya existe otro socio con el codigo indicado");
        }
        socio.setCodigo(dto.getCodigo());
        socio.setNombres(dto.getNombres());
        socio.setApellidos(dto.getApellidos());
        socio.setAccion(dto.getAccion());
        socio.setEtapa(dto.getEtapa());
        socio.setFechaNacimiento(dto.getFechaNacimiento());
        return toDTO(socioRepository.save(socio));
    }

    @Override
    public void eliminar(Long id) {
        socioRepository.delete(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public SocioDTO buscarPorId(Long id) {
        return toDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocioDTO> listar() {
        return socioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocioDTO> listarPorEtapas(List<Integer> etapas) {
        return socioRepository.findByEtapaIn(etapas).stream().map(this::toDTO).toList();
    }

    private Socio obtenerEntidad(Long id) {
        return socioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado: " + id));
    }

    private SocioDTO toDTO(Socio socio) {
        return objectMapper.convertValue(socio, SocioDTO.class);
    }
}
