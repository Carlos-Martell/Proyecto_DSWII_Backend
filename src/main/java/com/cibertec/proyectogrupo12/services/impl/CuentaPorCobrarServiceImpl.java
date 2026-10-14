package com.cibertec.proyectogrupo12.services.impl;

import com.cibertec.proyectogrupo12.entities.CuentaPorCobrar;
import com.cibertec.proyectogrupo12.entities.Puesto;
import com.cibertec.proyectogrupo12.entities.Servicio;
import com.cibertec.proyectogrupo12.entities.Socio;
import com.cibertec.proyectogrupo12.entities.enums.EstadoCuenta;
import com.cibertec.proyectogrupo12.exceptions.BusinessException;
import com.cibertec.proyectogrupo12.exceptions.ResourceNotFoundException;
import com.cibertec.proyectogrupo12.models.CuentaPorCobrarDTO;
import com.cibertec.proyectogrupo12.repositories.CuentaPorCobrarRepository;
import com.cibertec.proyectogrupo12.repositories.PuestoRepository;
import com.cibertec.proyectogrupo12.repositories.ServicioRepository;
import com.cibertec.proyectogrupo12.repositories.SocioRepository;
import com.cibertec.proyectogrupo12.services.CuentaPorCobrarService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Transactional
public class CuentaPorCobrarServiceImpl implements CuentaPorCobrarService {

    private final CuentaPorCobrarRepository cuentaRepository;
    private final ServicioRepository servicioRepository;
    private final SocioRepository socioRepository;
    private final PuestoRepository puestoRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<CuentaPorCobrarDTO> generarParaPuestos(Long servicioId, String periodo, List<Long> puestoIds) {
        Servicio servicio = obtenerServicio(servicioId);
        if (puestoIds == null || puestoIds.isEmpty()) {
            throw new BusinessException("Debe indicar al menos un puesto");
        }
        List<CuentaPorCobrar> generadas = puestoIds.stream()
                .map(this::obtenerPuesto)
                .map(puesto -> CuentaPorCobrar.builder()
                        .servicio(servicio)
                        .puesto(puesto)
                        .periodo(periodo)
                        .monto(servicio.getCosto())
                        .estado(EstadoCuenta.PENDIENTE)
                        .fechaGeneracion(LocalDate.now())
                        .build())
                .toList();
        return cuentaRepository.saveAll(generadas).stream().map(this::toDTO).toList();
    }

    @Override
    public CuentaPorCobrarDTO generarPorConsumo(Long servicioId, String periodo, Long puestoId,
                                                 BigDecimal lecturaInicial, BigDecimal lecturaFinal) {
        Servicio servicio = obtenerServicio(servicioId);
        if (!Boolean.TRUE.equals(servicio.getEsConsumo())) {
            throw new BusinessException("El servicio indicado no depende de consumo");
        }
        Puesto puesto = obtenerPuesto(puestoId);

        BigDecimal diferencia = lecturaFinal.subtract(lecturaInicial);
        BigDecimal monto = diferencia.compareTo(BigDecimal.ZERO) > 0
                ? diferencia.multiply(servicio.getCostoUnitario())
                : BigDecimal.ZERO;

        CuentaPorCobrar cuenta = CuentaPorCobrar.builder()
                .servicio(servicio)
                .puesto(puesto)
                .periodo(periodo)
                .monto(monto)
                .estado(EstadoCuenta.PENDIENTE)
                .lecturaInicial(lecturaInicial)
                .lecturaFinal(lecturaFinal)
                .fechaGeneracion(LocalDate.now())
                .build();
        return toDTO(cuentaRepository.save(cuenta));
    }

    @Override
    public List<CuentaPorCobrarDTO> generarParaSocios(Long servicioId, String periodo, BigDecimal monto,
                                                        List<Integer> etapas, boolean soloUnicos) {
        Servicio servicio = obtenerServicio(servicioId);
        BigDecimal montoAplicado = monto != null ? monto : servicio.getCosto();

        List<Socio> socios = (etapas != null && !etapas.isEmpty())
                ? socioRepository.findByEtapaIn(etapas)
                : socioRepository.findAll();

        if (soloUnicos) {
            Set<String> vistos = new HashSet<>();
            socios = socios.stream()
                    .filter(s -> vistos.add((s.getNombres() + "|" + s.getApellidos()).toLowerCase()))
                    .toList();
        }

        List<CuentaPorCobrar> generadas = socios.stream()
                .map(socio -> CuentaPorCobrar.builder()
                        .servicio(servicio)
                        .socio(socio)
                        .periodo(periodo)
                        .monto(montoAplicado)
                        .estado(EstadoCuenta.PENDIENTE)
                        .fechaGeneracion(LocalDate.now())
                        .build())
                .toList();
        return cuentaRepository.saveAll(generadas).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaPorCobrarDTO> listarPorSocio(Long socioId) {
        return cuentaRepository.findBySocioId(socioId).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaPorCobrarDTO> listarPorPuesto(Long puestoId) {
        return cuentaRepository.findByPuestoId(puestoId).stream().map(this::toDTO).toList();
    }

    @Override
    public CuentaPorCobrarDTO marcarAbonada(Long cuentaId) {
        CuentaPorCobrar cuenta = obtenerCuenta(cuentaId);
        cuenta.setEstado(EstadoCuenta.ABONADA);
        return toDTO(cuentaRepository.save(cuenta));
    }

    @Override
    public CuentaPorCobrarDTO marcarExonerada(Long cuentaId) {
        CuentaPorCobrar cuenta = obtenerCuenta(cuentaId);
        cuenta.setEstado(EstadoCuenta.EXONERADA);
        return toDTO(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotal(List<Long> cuentaIds) {
        return cuentaRepository.findAllById(cuentaIds).stream()
                .filter(c -> c.getEstado() == EstadoCuenta.ABONADA)
                .map(CuentaPorCobrar::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Servicio obtenerServicio(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + id));
    }

    private Puesto obtenerPuesto(Long id) {
        return puestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Puesto no encontrado: " + id));
    }

    private CuentaPorCobrar obtenerCuenta(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta por cobrar no encontrada: " + id));
    }

    private CuentaPorCobrarDTO toDTO(CuentaPorCobrar cuenta) {
        CuentaPorCobrarDTO dto = objectMapper.convertValue(cuenta, CuentaPorCobrarDTO.class);
        dto.setServicioId(cuenta.getServicio() != null ? cuenta.getServicio().getId() : null);
        dto.setSocioId(cuenta.getSocio() != null ? cuenta.getSocio().getId() : null);
        dto.setPuestoId(cuenta.getPuesto() != null ? cuenta.getPuesto().getId() : null);
        return dto;
    }
}
