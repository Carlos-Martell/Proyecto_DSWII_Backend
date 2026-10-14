package com.cibertec.proyectogrupo12.services;

import com.cibertec.proyectogrupo12.models.CuentaPorCobrarDTO;

import java.math.BigDecimal;
import java.util.List;


public interface CuentaPorCobrarService {

    List<CuentaPorCobrarDTO> generarParaPuestos(Long servicioId, String periodo, List<Long> puestoIds);

    CuentaPorCobrarDTO generarPorConsumo(Long servicioId, String periodo, Long puestoId,
                                          BigDecimal lecturaInicial, BigDecimal lecturaFinal);

    List<CuentaPorCobrarDTO> generarParaSocios(Long servicioId, String periodo, BigDecimal monto,
                                                List<Integer> etapas, boolean soloUnicos);

    List<CuentaPorCobrarDTO> listarPorSocio(Long socioId);

    List<CuentaPorCobrarDTO> listarPorPuesto(Long puestoId);

    CuentaPorCobrarDTO marcarAbonada(Long cuentaId);

    CuentaPorCobrarDTO marcarExonerada(Long cuentaId);

    BigDecimal calcularTotal(List<Long> cuentaIds);
}
