package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.BienDTO;
import com.fireequipmanager.backend.service.BienService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bienes")
@CrossOrigin(origins = "*")
public class BienController {

    private final BienService bienService;

    public BienController(BienService bienService) {
        this.bienService = bienService;
    }

    // Lista todos los bienes
    @GetMapping
    public ResponseEntity<List<BienDTO>> listarTodos() {

        return ResponseEntity.ok(
                bienService.listarTodos()
        );
    }

    // Lista únicamente los bienes activos
    @GetMapping("/activos")
    public ResponseEntity<List<BienDTO>> listarActivos() {

        return ResponseEntity.ok(
                bienService.listarActivos()
        );
    }

    // Busca un bien por ID
    @GetMapping("/{id}")
    public ResponseEntity<BienDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bienService.buscarPorId(id)
        );
    }

    // Registra un nuevo bien
    @PostMapping
    public ResponseEntity<BienDTO> crear(
            @Valid @RequestBody BienDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        bienService.crear(dto)
                );
    }

    // Actualiza un bien
    @PutMapping("/{id}")
    public ResponseEntity<BienDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody BienDTO dto) {

        return ResponseEntity.ok(
                bienService.actualizar(id, dto)
        );
    }

    // Activa un bien
    @PatchMapping("/{id}/activar")
    public ResponseEntity<BienDTO> activar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bienService.activar(id)
        );
    }

    // Desactiva un bien
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<BienDTO> desactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bienService.desactivar(id)
        );
    }

    // Elimina un bien
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        bienService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}