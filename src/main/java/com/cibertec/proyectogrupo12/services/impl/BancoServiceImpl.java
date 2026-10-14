package com.cibertec.proyectogrupo12.services.impl;

import com.cibertec.proyectogrupo12.entities.Banco;
import com.cibertec.proyectogrupo12.exceptions.ResourceNotFoundException;
import com.cibertec.proyectogrupo12.models.BancoDTO;
import com.cibertec.proyectogrupo12.repositories.BancoRepository;
import com.cibertec.proyectogrupo12.services.BancoService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BancoServiceImpl implements BancoService {

    private final BancoRepository bancoRepository;
    private final ObjectMapper objectMapper;

    @Override
    public BancoDTO crear(BancoDTO dto) {
        Banco banco = objectMapper.convertValue(dto, Banco.class);
        banco.setId(null);
        return toDTO(bancoRepository.save(banco));
    }

    @Override
    public BancoDTO editar(Long id, BancoDTO dto) {
        Banco banco = obtenerEntidad(id);
        banco.setNombre(dto.getNombre());
        banco.setNumeroCuenta(dto.getNumeroCuenta());
        banco.setCci(dto.getCci());
        banco.setMoneda(dto.getMoneda());
        return toDTO(bancoRepository.save(banco));
    }

    @Override
    public void eliminar(Long id) {
        bancoRepository.delete(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public BancoDTO buscarPorId(Long id) {
        return toDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BancoDTO> listar() {
        return bancoRepository.findAll().stream().map(this::toDTO).toList();
    }

    private Banco obtenerEntidad(Long id) {
        return bancoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banco no encontrado: " + id));
    }

    private BancoDTO toDTO(Banco banco) {
        return objectMapper.convertValue(banco, BancoDTO.class);
    }
}
