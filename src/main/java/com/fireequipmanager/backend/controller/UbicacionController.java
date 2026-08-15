package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.UbicacionDTO;
import com.fireequipmanager.backend.model.enumsUbicacion.NombreUbicacion;
import com.fireequipmanager.backend.service.UbicacionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/ubicaciones")
@CrossOrigin(origins = "*")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    // Lista todas las ubicaciones
    @GetMapping
    public ResponseEntity<List<UbicacionDTO>> listarTodos() {

        return ResponseEntity.ok(
                ubicacionService.listarTodos()
        );
    }

    // Lista únicamente las ubicaciones activas
    @GetMapping("/activas")
    public ResponseEntity<List<UbicacionDTO>> listarActivas() {

        return ResponseEntity.ok(
                ubicacionService.listarActivas()
        );
    }

    // Busca una ubicación por ID
    @GetMapping("/{id}")
    public ResponseEntity<UbicacionDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ubicacionService.buscarPorId(Objects.requireNonNull(id, "El ID de la ubicación no puede ser nulo"))
        );
    }

    // Busca una ubicación por nombre
    @GetMapping("/nombre/{nombreUbicacion}")
    public ResponseEntity<UbicacionDTO> buscarPorNombre(
            @PathVariable NombreUbicacion nombreUbicacion) {

        return ResponseEntity.ok(
                ubicacionService.buscarPorNombre(Objects.requireNonNull(nombreUbicacion, "El nombre de la ubicación no puede ser nulo"))
        );
    }

    // Lista ubicaciones activas por tipo
    @GetMapping("/tipo/{nombreUbicacion}")
    public ResponseEntity<List<UbicacionDTO>> listarPorTipo(
            @PathVariable NombreUbicacion nombreUbicacion) {

        return ResponseEntity.ok(
                ubicacionService.listarPorTipo(Objects.requireNonNull(nombreUbicacion, "El nombre de la ubicación no puede ser nulo"))
        );
    }

    // Registra una nueva ubicación
    @PostMapping
    public ResponseEntity<UbicacionDTO> crear(
            @Valid @RequestBody UbicacionDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ubicacionService.crear(dto)
                );
    }

    // Actualiza una ubicación
    @PutMapping("/{id}")
    public ResponseEntity<UbicacionDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UbicacionDTO dto) {

        return ResponseEntity.ok(
                ubicacionService.actualizar(Objects.requireNonNull(id, "El ID de la ubicación no puede ser nulo"), Objects.requireNonNull(dto, "El DTO de la ubicación no puede ser nulo"))
        );
    }

    // Activa una ubicación
    @PatchMapping("/{id}/activar")
    public ResponseEntity<UbicacionDTO> activar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ubicacionService.activar(Objects.requireNonNull(id, "El ID de la ubicación no puede ser nulo"))
        );
    }

    // Desactiva una ubicación
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<UbicacionDTO> desactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ubicacionService.desactivar(Objects.requireNonNull(id, "El ID de la ubicación no puede ser nulo"))
        );
    }

    // Elimina una ubicación
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        ubicacionService.eliminar(Objects.requireNonNull(id, "El ID de la ubicación no puede ser nulo"));

        return ResponseEntity.noContent().build();
    }
}