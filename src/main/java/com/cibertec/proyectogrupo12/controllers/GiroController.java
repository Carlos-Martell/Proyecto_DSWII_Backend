package com.cibertec.proyectogrupo12.controllers;

import com.cibertec.proyectogrupo12.models.GiroDTO;
import com.cibertec.proyectogrupo12.services.GiroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/giros")
@RequiredArgsConstructor
public class GiroController {

    private final GiroService giroService;

    @GetMapping
    public ResponseEntity<List<GiroDTO>> listar() {
        return ResponseEntity.ok(giroService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiroDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(giroService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<GiroDTO> crear(@Valid @RequestBody GiroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(giroService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GiroDTO> editar(@PathVariable Long id, @Valid @RequestBody GiroDTO dto) {
        return ResponseEntity.ok(giroService.editar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        giroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
