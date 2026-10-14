package com.cibertec.proyectogrupo12.services;

import com.cibertec.proyectogrupo12.entities.enums.TipoRecibo;
import com.cibertec.proyectogrupo12.models.ReciboDTO;

import java.time.LocalDateTime;
import java.util.List;


public interface ReciboService {

    List<ReciboDTO> procesarPago(List<Long> cuentaIds, Long usuarioId);

    ReciboDTO canjearPorBanco(Long cuentaId, Long bancoId, java.time.LocalDate fechaDeposito, Long usuarioId);

    ReciboDTO registrarIngresoExterno(ReciboDTO dto);

    List<ReciboDTO> listarPorTipoYFecha(TipoRecibo tipo, LocalDateTime desde, LocalDateTime hasta);

    ReciboDTO buscarPorId(Long id);

    List<ReciboDTO> listarPorSocio(Long socioId);

    List<ReciboDTO> listarPorPuesto(Long puestoId);
}
