package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.CompartimientoVehiculoDTO;
import com.fireequipmanager.backend.service.CompartimientoVehiculoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/compartimientos-vehiculo")
@CrossOrigin(origins = "*")
public class CompartimientoVehiculoController {

    private final CompartimientoVehiculoService compartimientoVehiculoService;

    public CompartimientoVehiculoController(
            CompartimientoVehiculoService compartimientoVehiculoService) {

        this.compartimientoVehiculoService =
                compartimientoVehiculoService;
    }

    // Lista todos los compartimientos
    @GetMapping
    public ResponseEntity<List<CompartimientoVehiculoDTO>> listarTodos() {

        return ResponseEntity.ok(
                compartimientoVehiculoService.listarTodos()
        );
    }

    // Lista los compartimientos activos
    @GetMapping("/activos")
    public ResponseEntity<List<CompartimientoVehiculoDTO>> listarActivos() {

        return ResponseEntity.ok(
                compartimientoVehiculoService.listarActivos()
        );
    }

    // Busca un compartimiento por ID
    @GetMapping("/{id}")
    public ResponseEntity<CompartimientoVehiculoDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                compartimientoVehiculoService.buscarPorId(Objects.requireNonNull(id, "El ID del compartimiento no puede ser nulo"))
        );
    }

    // Lista los compartimientos de un vehículo
    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<CompartimientoVehiculoDTO>> listarPorVehiculo(
            @PathVariable Long vehiculoId) {

        return ResponseEntity.ok(
                compartimientoVehiculoService.listarPorVehiculo(Objects.requireNonNull(vehiculoId, "El ID del vehículo no puede ser nulo"))
        );
    }

    // Registra un nuevo compartimiento
    @PostMapping
    public ResponseEntity<CompartimientoVehiculoDTO> crear(
            @Valid @RequestBody CompartimientoVehiculoDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        compartimientoVehiculoService.crear(Objects.requireNonNull(dto, "El DTO del compartimiento no puede ser nulo"))
                );
    }

    // Actualiza un compartimiento
    @PutMapping("/{id}")
    public ResponseEntity<CompartimientoVehiculoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CompartimientoVehiculoDTO dto) {

        return ResponseEntity.ok(
                compartimientoVehiculoService.actualizar(Objects.requireNonNull(id, "El ID del compartimiento no puede ser nulo"), Objects.requireNonNull(dto, "El DTO del compartimiento no puede ser nulo"))
        );
    }

    // Activa un compartimiento
    @PatchMapping("/{id}/activar")
    public ResponseEntity<CompartimientoVehiculoDTO> activar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                compartimientoVehiculoService.activar(Objects.requireNonNull(id, "El ID del compartimiento no puede ser nulo"))
        );
    }

    // Desactiva un compartimiento
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<CompartimientoVehiculoDTO> desactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                compartimientoVehiculoService.desactivar(Objects.requireNonNull(id, "El ID del compartimiento no puede ser nulo"))
        );
    }

    // Elimina un compartimiento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        compartimientoVehiculoService.eliminar(Objects.requireNonNull(id, "El ID del compartimiento no puede ser nulo"));

        return ResponseEntity.noContent().build();
    }
}