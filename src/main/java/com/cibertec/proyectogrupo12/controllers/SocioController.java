package com.cibertec.proyectogrupo12.controllers;

import com.cibertec.proyectogrupo12.models.ResumenCuentasDTO;
import com.cibertec.proyectogrupo12.models.SocioDTO;
import com.cibertec.proyectogrupo12.services.CuentaPorCobrarService;
import com.cibertec.proyectogrupo12.services.ReciboService;
import com.cibertec.proyectogrupo12.services.SocioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/socios")
@RequiredArgsConstructor
public class SocioController {

    private final SocioService socioService;
    private final CuentaPorCobrarService cuentaPorCobrarService;
    private final ReciboService reciboService;

    @GetMapping
    public ResponseEntity<List<SocioDTO>> listar() {
        return ResponseEntity.ok(socioService.listar());
    }

    @GetMapping("/etapas")
    public ResponseEntity<List<SocioDTO>> listarPorEtapas(@RequestParam List<Integer> etapas) {
        return ResponseEntity.ok(socioService.listarPorEtapas(etapas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SocioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(socioService.buscarPorId(id));
    }

      @GetMapping("/{id}/resumen")
    public ResponseEntity<ResumenCuentasDTO> resumen(@PathVariable Long id) {
        socioService.buscarPorId(id);
        ResumenCuentasDTO resumen = ResumenCuentasDTO.builder()
                .cuentas(cuentaPorCobrarService.listarPorSocio(id))
                .movimientos(reciboService.listarPorSocio(id))
                .build();
        return ResponseEntity.ok(resumen);
    }

    @PostMapping
    public ResponseEntity<SocioDTO> crear(@Valid @RequestBody SocioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(socioService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SocioDTO> editar(@PathVariable Long id, @Valid @RequestBody SocioDTO dto) {
        return ResponseEntity.ok(socioService.editar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        socioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

