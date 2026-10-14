package com.cibertec.proyectogrupo12.controllers;

import com.cibertec.proyectogrupo12.exceptions.BusinessException;
import com.cibertec.proyectogrupo12.models.EgresoCargaMasivaResultado;
import com.cibertec.proyectogrupo12.models.EgresoDTO;
import com.cibertec.proyectogrupo12.services.EgresoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/egresos")
@RequiredArgsConstructor
public class EgresoController {

    private final EgresoService egresoService;

    @PostMapping
    public ResponseEntity<EgresoDTO> registrar(@Valid @RequestBody EgresoDTO dto,
                                                @RequestParam Long usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(egresoService.registrar(dto, usuarioId));
    }

    @PostMapping(value = "/masivo", consumes = "multipart/form-data")
    public ResponseEntity<EgresoCargaMasivaResultado> registrarMasivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam Long usuarioId) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("Debe seleccionar un archivo");
        }
        try {
            EgresoCargaMasivaResultado resultado = egresoService.registrarMasivo(
                    archivo.getInputStream(), archivo.getOriginalFilename(), usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (IOException e) {
            throw new BusinessException("No se pudo leer el archivo enviado: " + e.getMessage());
        }
    }

    @GetMapping("/mes")
    public ResponseEntity<List<EgresoDTO>> listarPorMes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(egresoService.listarPorMes(fecha));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EgresoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(egresoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<EgresoDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(egresoService.anular(id));
    }

    @PatchMapping("/{id}/procesar")
    public ResponseEntity<EgresoDTO> procesar(@PathVariable Long id) {
        return ResponseEntity.ok(egresoService.procesar(id));
    }
}
