package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.VehiculoDTO;
import com.fireequipmanager.backend.service.VehiculoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    // Lista todos los vehículos
    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> listarTodos() {

        return ResponseEntity.ok(
                vehiculoService.listarTodos()
        );
    }

    // Lista únicamente los vehículos activos
    @GetMapping("/activos")
    public ResponseEntity<List<VehiculoDTO>> listarActivos() {

        return ResponseEntity.ok(
                vehiculoService.listarActivos()
        );
    }
        // Lista únicamente los vehículos activos
    @GetMapping("/activos/operativos")
    public ResponseEntity<List<VehiculoDTO>> listarActivosOperativos() {

        return ResponseEntity.ok(
                vehiculoService.listarActivosOperativos()
        );
    }


    // Busca un vehículo por ID
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                vehiculoService.buscarPorId(Objects.requireNonNull(id, "El ID del vehículo no puede ser nulo"))
        );
    }

    // Registra un nuevo vehículo
    @PostMapping
    public ResponseEntity<VehiculoDTO> crear(
            @Valid @RequestBody VehiculoDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        vehiculoService.crear(Objects.requireNonNull(dto, "El DTO del vehículo no puede ser nulo"))
                );
    }

    // Actualiza un vehículo
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehiculoDTO dto) {

        return ResponseEntity.ok(
                vehiculoService.actualizar(Objects.requireNonNull(id, "El ID del vehículo no puede ser nulo"), Objects.requireNonNull(dto, "El DTO del vehículo no puede ser nulo"))
        );
    }

    // Activa un vehículo
    @PatchMapping("/{id}/activar")
    public ResponseEntity<VehiculoDTO> activar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                vehiculoService.activar(Objects.requireNonNull(id, "El ID del vehículo no puede ser nulo"))
        );
    }

    // Desactiva un vehículo
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<VehiculoDTO> desactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                vehiculoService.desactivar(Objects.requireNonNull(id, "El ID del vehículo no puede ser nulo"))
        );
    }

    // Elimina un vehículo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        vehiculoService.eliminar(Objects.requireNonNull(id, "El ID del vehículo no puede ser nulo"));

        return ResponseEntity.noContent().build();
    }
}