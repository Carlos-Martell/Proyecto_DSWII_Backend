package com.cibertec.proyectogrupo12.controllers;

import com.cibertec.proyectogrupo12.models.BancoDTO;
import com.cibertec.proyectogrupo12.services.BancoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/bancos")
@RequiredArgsConstructor
public class BancoController {

    private final BancoService bancoService;

    @GetMapping
    public ResponseEntity<List<BancoDTO>> listar() {
        return ResponseEntity.ok(bancoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BancoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bancoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<BancoDTO> crear(@Valid @RequestBody BancoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bancoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BancoDTO> editar(@PathVariable Long id, @Valid @RequestBody BancoDTO dto) {
        return ResponseEntity.ok(bancoService.editar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        bancoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
