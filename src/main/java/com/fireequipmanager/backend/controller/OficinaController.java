package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.OficinaDTO;
import com.fireequipmanager.backend.service.OficinaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/oficinas")
@CrossOrigin(origins = "*")
public class OficinaController {

    private final OficinaService oficinaService;

    public OficinaController(OficinaService oficinaService) {
        this.oficinaService = oficinaService;
    }

    // Lista todas las oficinas
    @GetMapping
    public ResponseEntity<List<OficinaDTO>> listarTodos() {

        return ResponseEntity.ok(
                oficinaService.listarTodos()
        );
    }

    // Lista únicamente las oficinas activas
    @GetMapping("/activas")
    public ResponseEntity<List<OficinaDTO>> listarActivas() {

        return ResponseEntity.ok(
                oficinaService.listarActivas()
        );
    }

    // Busca una oficina por ID
    @GetMapping("/{id}")
    public ResponseEntity<OficinaDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                oficinaService.buscarPorId(Objects.requireNonNull(id, "El ID de la oficina no puede ser nulo"))
        );
    }

    // Lista oficinas por piso
    @GetMapping("/piso/{piso}")
    public ResponseEntity<List<OficinaDTO>> listarPorPiso(
            @PathVariable String piso) {

        return ResponseEntity.ok(
                oficinaService.listarPorPiso(piso)
        );
    }

    // Registra una nueva oficina
    @PostMapping
    public ResponseEntity<OficinaDTO> crear(
            @Valid @RequestBody OficinaDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        oficinaService.crear(Objects.requireNonNull(dto, "El DTO de la oficina no puede ser nulo"))
                );
    }

    // Actualiza una oficina
    @PutMapping("/{id}")
    public ResponseEntity<OficinaDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody OficinaDTO dto) {

        return ResponseEntity.ok(
                oficinaService.actualizar(Objects.requireNonNull(id, "El ID de la oficina no puede ser nulo"), Objects.requireNonNull(dto, "El DTO de la oficina no puede ser nulo"))
        );
    }

    // Activa una oficina
    @PatchMapping("/{id}/activar")
    public ResponseEntity<OficinaDTO> activar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                oficinaService.activar(Objects.requireNonNull(id, "El ID de la oficina no puede ser nulo"))
        );
    }

    // Desactiva una oficina
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<OficinaDTO> desactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                oficinaService.desactivar(Objects.requireNonNull(id, "El ID de la oficina no puede ser nulo"))
        );
    }

    // Elimina una oficina
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        oficinaService.eliminar(Objects.requireNonNull(id, "El ID de la oficina no puede ser nulo"));

        return ResponseEntity.noContent().build();
    }
}