package com.cibertec.proyectogrupo12.controllers;

import com.cibertec.proyectogrupo12.models.PuestoDTO;
import com.cibertec.proyectogrupo12.models.ResumenCuentasDTO;
import com.cibertec.proyectogrupo12.services.CuentaPorCobrarService;
import com.cibertec.proyectogrupo12.services.PuestoService;
import com.cibertec.proyectogrupo12.services.ReciboService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/puestos")
@RequiredArgsConstructor
public class PuestoController {

    private final PuestoService puestoService;
    private final CuentaPorCobrarService cuentaPorCobrarService;
    private final ReciboService reciboService;

    @GetMapping
    public ResponseEntity<List<PuestoDTO>> listar() {
        return ResponseEntity.ok(puestoService.listar());
    }

    @GetMapping("/socio/{socioId}")
    public ResponseEntity<List<PuestoDTO>> listarPorSocio(@PathVariable Long socioId) {
        return ResponseEntity.ok(puestoService.listarPorSocio(socioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PuestoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(puestoService.buscarPorId(id));
    }


    @GetMapping("/{id}/resumen")
    public ResponseEntity<ResumenCuentasDTO> resumen(@PathVariable Long id) {
        puestoService.buscarPorId(id);
        ResumenCuentasDTO resumen = ResumenCuentasDTO.builder()
                .cuentas(cuentaPorCobrarService.listarPorPuesto(id))
                .movimientos(reciboService.listarPorPuesto(id))
                .build();
        return ResponseEntity.ok(resumen);
    }

    @PostMapping
    public ResponseEntity<PuestoDTO> crear(@Valid @RequestBody PuestoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(puestoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PuestoDTO> editar(@PathVariable Long id, @Valid @RequestBody PuestoDTO dto) {
        return ResponseEntity.ok(puestoService.editar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        puestoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

