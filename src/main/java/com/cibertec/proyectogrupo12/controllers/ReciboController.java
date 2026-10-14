package com.cibertec.proyectogrupo12.controllers;

import com.cibertec.proyectogrupo12.entities.enums.TipoRecibo;
import com.cibertec.proyectogrupo12.models.CanjeBancoRequest;
import com.cibertec.proyectogrupo12.models.ProcesarPagoRequest;
import com.cibertec.proyectogrupo12.models.ReciboDTO;
import com.cibertec.proyectogrupo12.services.ReciboService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/recibos")
@RequiredArgsConstructor
public class ReciboController {

    private final ReciboService reciboService;

    @PostMapping("/pagos")
    public ResponseEntity<List<ReciboDTO>> procesarPago(@Valid @RequestBody ProcesarPagoRequest request) {
        List<ReciboDTO> recibos = reciboService.procesarPago(request.getCuentaIds(), request.getUsuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(recibos);
    }

     @PostMapping("/canje")
    public ResponseEntity<ReciboDTO> canjearPorBanco(@Valid @RequestBody CanjeBancoRequest request) {
        ReciboDTO recibo = reciboService.canjearPorBanco(
                request.getCuentaId(), request.getBancoId(), request.getFechaDeposito(), request.getUsuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(recibo);
    }

    @PostMapping("/ingresos-externos")
    public ResponseEntity<ReciboDTO> registrarIngresoExterno(@Valid @RequestBody ReciboDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reciboService.registrarIngresoExterno(dto));
    }

    @GetMapping
    public ResponseEntity<List<ReciboDTO>> listarPorTipoYFecha(
            @RequestParam TipoRecibo tipo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(reciboService.listarPorTipoYFecha(tipo, desde, hasta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReciboDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reciboService.buscarPorId(id));
    }
}
