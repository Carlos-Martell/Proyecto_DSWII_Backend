package com.cibertec.proyectogrupo12.services.impl;

import com.cibertec.proyectogrupo12.entities.Banco;
import com.cibertec.proyectogrupo12.entities.CuentaPorCobrar;
import com.cibertec.proyectogrupo12.entities.Recibo;
import com.cibertec.proyectogrupo12.entities.Usuario;
import com.cibertec.proyectogrupo12.entities.enums.EstadoCuenta;
import com.cibertec.proyectogrupo12.entities.enums.TipoRecibo;
import com.cibertec.proyectogrupo12.exceptions.BusinessException;
import com.cibertec.proyectogrupo12.exceptions.ResourceNotFoundException;
import com.cibertec.proyectogrupo12.models.ReciboDTO;
import com.cibertec.proyectogrupo12.repositories.BancoRepository;
import com.cibertec.proyectogrupo12.repositories.CuentaPorCobrarRepository;
import com.cibertec.proyectogrupo12.repositories.ReciboRepository;
import com.cibertec.proyectogrupo12.repositories.UsuarioRepository;
import com.cibertec.proyectogrupo12.services.ReciboService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class ReciboServiceImpl implements ReciboService {

    private final ReciboRepository reciboRepository;
    private final CuentaPorCobrarRepository cuentaRepository;
    private final BancoRepository bancoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<ReciboDTO> procesarPago(List<Long> cuentaIds, Long usuarioId) {
        if (cuentaIds == null || cuentaIds.isEmpty()) {
            throw new BusinessException("Debe seleccionar al menos una cuenta");
        }
        Usuario usuario = obtenerUsuario(usuarioId);

        List<CuentaPorCobrar> cuentas = cuentaRepository.findAllById(cuentaIds).stream()
                .filter(c -> c.getEstado() == EstadoCuenta.ABONADA && c.getFechaPago() == null)
                .toList();
        if (cuentas.isEmpty()) {
            throw new BusinessException("Ninguna de las cuentas indicadas esta pendiente de pago (deben estar abonadas y sin fecha de pago)");
        }

        List<Recibo> recibos = cuentas.stream().map(cuenta -> {
            cuenta.setFechaPago(LocalDate.now());
            cuentaRepository.save(cuenta);
            return Recibo.builder()
                    .correlativo(generarCorrelativo(TipoRecibo.INGRESO))
                    .tipo(TipoRecibo.INGRESO)
                    .fecha(LocalDateTime.now())
                    .monto(cuenta.getMonto())
                    .cuenta(cuenta)
                    .usuario(usuario)
                    .build();
        }).toList();

        return reciboRepository.saveAll(recibos).stream().map(this::toDTO).toList();
    }

    @Override
    public ReciboDTO canjearPorBanco(Long cuentaId, Long bancoId, LocalDate fechaDeposito, Long usuarioId) {
        CuentaPorCobrar cuenta = obtenerCuenta(cuentaId);
        if (cuenta.getSocio() == null) {
            throw new BusinessException("Solo se pueden canjear cuentas de socio");
        }
        if (cuenta.getEstado() != EstadoCuenta.PENDIENTE) {
            throw new BusinessException("Solo se pueden canjear cuentas pendientes (ya fue pagada o exonerada)");
        }
        Banco banco = obtenerBanco(bancoId);
        Usuario usuario = obtenerUsuario(usuarioId);

        cuenta.setEstado(EstadoCuenta.ABONADA);
        cuenta.setFechaPago(LocalDate.now());
        cuentaRepository.save(cuenta);

        Recibo recibo = Recibo.builder()
                .correlativo(generarCorrelativo(TipoRecibo.BANCO))
                .tipo(TipoRecibo.BANCO)
                .fecha(LocalDateTime.now())
                .monto(cuenta.getMonto())
                .cuenta(cuenta)
                .banco(banco)
                .usuario(usuario)
                .fechaDeposito(fechaDeposito)
                .build();
        return toDTO(reciboRepository.save(recibo));
    }

    @Override
    public ReciboDTO registrarIngresoExterno(ReciboDTO dto) {
        Usuario usuario = obtenerUsuario(dto.getUsuarioId());
        Recibo recibo = Recibo.builder()
                .correlativo(generarCorrelativo(TipoRecibo.INGRESO))
                .tipo(TipoRecibo.INGRESO)
                .fecha(LocalDateTime.now())
                .monto(dto.getMonto())
                .usuario(usuario)
                .depositante(dto.getDepositante())
                .categoria(dto.getCategoria())
                .concepto(dto.getConcepto())
                .build();
        return toDTO(reciboRepository.save(recibo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReciboDTO> listarPorTipoYFecha(TipoRecibo tipo, LocalDateTime desde, LocalDateTime hasta) {
        return reciboRepository.findByTipoAndFechaBetween(tipo, desde, hasta).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReciboDTO buscarPorId(Long id) {
        return toDTO(reciboRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReciboDTO> listarPorSocio(Long socioId) {
        List<Long> cuentaIds = cuentaRepository.findBySocioId(socioId).stream().map(CuentaPorCobrar::getId).toList();
        if (cuentaIds.isEmpty()) {
            return List.of();
        }
        return reciboRepository.findByCuentaIdIn(cuentaIds).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReciboDTO> listarPorPuesto(Long puestoId) {
        List<Long> cuentaIds = cuentaRepository.findByPuestoId(puestoId).stream().map(CuentaPorCobrar::getId).toList();
        if (cuentaIds.isEmpty()) {
            return List.of();
        }
        return reciboRepository.findByCuentaIdIn(cuentaIds).stream().map(this::toDTO).toList();
    }


    private synchronized String generarCorrelativo(TipoRecibo tipo) {
        String prefijo = switch (tipo) {
            case INGRESO -> "ING";
            case EGRESO -> "EGR";
            case BANCO -> "BAN";
        };
        long siguiente = reciboRepository.findTopByTipoOrderByIdDesc(tipo)
                .map(ultimo -> extraerNumero(ultimo.getCorrelativo()) + 1)
                .orElse(1L);
        return prefijo + "-" + String.format("%06d", siguiente);
    }

    private long extraerNumero(String correlativo) {
        String[] partes = correlativo.split("-");
        try {
            return Long.parseLong(partes[partes.length - 1]);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private CuentaPorCobrar obtenerCuenta(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta por cobrar no encontrada: " + id));
    }

    private Banco obtenerBanco(Long id) {
        return bancoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banco no encontrado: " + id));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    private ReciboDTO toDTO(Recibo recibo) {
        ReciboDTO dto = objectMapper.convertValue(recibo, ReciboDTO.class);
        dto.setCuentaId(recibo.getCuenta() != null ? recibo.getCuenta().getId() : null);
        dto.setBancoId(recibo.getBanco() != null ? recibo.getBanco().getId() : null);
        dto.setUsuarioId(recibo.getUsuario() != null ? recibo.getUsuario().getId() : null);
        return dto;
    }
}
