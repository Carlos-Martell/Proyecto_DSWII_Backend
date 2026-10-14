package com.cibertec.proyectogrupo12.controllers;

import com.cibertec.proyectogrupo12.models.*;
import com.cibertec.proyectogrupo12.services.CuentaPorCobrarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas-por-cobrar")
@RequiredArgsConstructor
public class CuentaPorCobrarController {

    private final CuentaPorCobrarService cuentaPorCobrarService;

    @PostMapping("/generar/puestos")
    public ResponseEntity<List<CuentaPorCobrarDTO>> generarParaPuestos(
            @Valid @RequestBody GenerarCuentasPuestoRequest request) {
        List<CuentaPorCobrarDTO> cuentas = cuentaPorCobrarService.generarParaPuestos(
                request.getServicioId(), request.getPeriodo(), request.getPuestoIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentas);
    }

    @PostMapping("/generar/consumo")
    public ResponseEntity<CuentaPorCobrarDTO> generarPorConsumo(
            @Valid @RequestBody GenerarCuentaConsumoRequest request) {
        CuentaPorCobrarDTO cuenta = cuentaPorCobrarService.generarPorConsumo(
                request.getServicioId(), request.getPeriodo(), request.getPuestoId(),
                request.getLecturaInicial(), request.getLecturaFinal());
        return ResponseEntity.status(HttpStatus.CREATED).body(cuenta);
    }

    @PostMapping("/generar/socios")
    public ResponseEntity<List<CuentaPorCobrarDTO>> generarParaSocios(
            @Valid @RequestBody GenerarCuentasSocioRequest request) {
        List<CuentaPorCobrarDTO> cuentas = cuentaPorCobrarService.generarParaSocios(
                request.getServicioId(), request.getPeriodo(), request.getMonto(),
                request.getEtapas(), request.isSoloUnicos());
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentas);
    }

    @GetMapping("/socio/{socioId}")
    public ResponseEntity<List<CuentaPorCobrarDTO>> listarPorSocio(@PathVariable Long socioId) {
        return ResponseEntity.ok(cuentaPorCobrarService.listarPorSocio(socioId));
    }

    @GetMapping("/puesto/{puestoId}")
    public ResponseEntity<List<CuentaPorCobrarDTO>> listarPorPuesto(@PathVariable Long puestoId) {
        return ResponseEntity.ok(cuentaPorCobrarService.listarPorPuesto(puestoId));
    }

    @PatchMapping("/{id}/abonar")
    public ResponseEntity<CuentaPorCobrarDTO> marcarAbonada(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaPorCobrarService.marcarAbonada(id));
    }

    @PatchMapping("/{id}/exonerar")
    public ResponseEntity<CuentaPorCobrarDTO> marcarExonerada(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaPorCobrarService.marcarExonerada(id));
    }

    @PostMapping("/total")
    public ResponseEntity<TotalResponse> calcularTotal(@Valid @RequestBody TotalCuentasRequest request) {
        var total = cuentaPorCobrarService.calcularTotal(request.getCuentaIds());
        return ResponseEntity.ok(TotalResponse.builder().total(total).build());
    }
}
