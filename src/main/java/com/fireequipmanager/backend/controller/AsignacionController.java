package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.AsignacionDTO;
import com.fireequipmanager.backend.model.enumsAsignacion.EstadoAsignacion;
import com.fireequipmanager.backend.service.AsignacionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AsignacionController {

    private final AsignacionService asignacionService;

    // ==========================
    // ENDPOINTS DE CONSULTA (GET)
    // ==========================

    /**
     * Listar todas las asignaciones
     * GET /api/asignaciones
     */
    @GetMapping
    public ResponseEntity<List<AsignacionDTO>> listarTodos() {
        log.info("Solicitud para listar todas las asignaciones");
        List<AsignacionDTO> asignaciones = asignacionService.listarTodos();
        return ResponseEntity.ok(asignaciones);
    }

    /**
     * Listar solo asignaciones activas
     * GET /api/asignaciones/activas
     */
    @GetMapping("/activas")
    public ResponseEntity<List<AsignacionDTO>> listarActivas() {
        log.info("Solicitud para listar asignaciones activas");
        List<AsignacionDTO> asignaciones = asignacionService.listarActivas();
        return ResponseEntity.ok(asignaciones);
    }

    /**
     * Listar asignaciones por estado
     * GET /api/asignaciones/estado/{estado}
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<AsignacionDTO>> listarPorEstado(@PathVariable EstadoAsignacion estado) {
        log.info("Solicitud para listar asignaciones con estado: {}", estado);
        List<AsignacionDTO> asignaciones = asignacionService.listarPorEstado(estado);
        return ResponseEntity.ok(asignaciones);
    }

    /**
     * Buscar asignación por ID
     * GET /api/asignaciones/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AsignacionDTO> buscarPorId(@PathVariable Long id) {
        log.info("Solicitud para buscar asignación con ID: {}", id);
        AsignacionDTO asignacion = asignacionService.buscarPorId(id);
        return ResponseEntity.ok(asignacion);
    }

    /**
     * Listar asignaciones por bombero
     * GET /api/asignaciones/bombero/{bomberoId}
     */
    @GetMapping("/bombero/{bomberoId}")
    public ResponseEntity<List<AsignacionDTO>> listarPorBombero(@PathVariable Long bomberoId) {
        log.info("Solicitud para listar asignaciones del bombero con ID: {}", bomberoId);
        List<AsignacionDTO> asignaciones = asignacionService.listarPorBombero(bomberoId);
        return ResponseEntity.ok(asignaciones);
    }

    /**
     * Listar asignaciones por equipo
     * GET /api/asignaciones/equipo/{equipoId}
     */
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<AsignacionDTO>> listarPorEquipo(@PathVariable Long equipoId) {
        log.info("Solicitud para listar asignaciones del equipo con ID: {}", equipoId);
        List<AsignacionDTO> asignaciones = asignacionService.listarPorEquipo(equipoId);
        return ResponseEntity.ok(asignaciones);
    }

    /**
     * Listar asignaciones por EPP
     * GET /api/asignaciones/epp/{eppId}
     */
    @GetMapping("/epp/{eppId}")
    public ResponseEntity<List<AsignacionDTO>> listarPorEpp(@PathVariable Long eppId) {
        log.info("Solicitud para listar asignaciones del EPP con ID: {}", eppId);
        List<AsignacionDTO> asignaciones = asignacionService.listarPorEpp(eppId);
        return ResponseEntity.ok(asignaciones);
    }

    /**
     * Listar asignaciones por vehículo
     * GET /api/asignaciones/vehiculo/{vehiculoId}
     */
    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<AsignacionDTO>> listarPorVehiculo(@PathVariable Long vehiculoId) {
        log.info("Solicitud para listar asignaciones del vehículo con ID: {}", vehiculoId);
        List<AsignacionDTO> asignaciones = asignacionService.listarPorVehiculo(vehiculoId);
        return ResponseEntity.ok(asignaciones);
    }

    /**
     * Listar asignaciones por ubicación
     * GET /api/asignaciones/ubicacion/{ubicacionId}
     */
    @GetMapping("/ubicacion/{ubicacionId}")
    public ResponseEntity<List<AsignacionDTO>> listarPorUbicacion(@PathVariable Long ubicacionId) {
        log.info("Solicitud para listar asignaciones de la ubicación con ID: {}", ubicacionId);
        List<AsignacionDTO> asignaciones = asignacionService.listarPorUbicacion(ubicacionId);
        return ResponseEntity.ok(asignaciones);
    }

    // ==========================
    // ENDPOINTS DE CREACIÓN (POST)
    // ==========================

    /**
     * Crear una nueva asignación
     * POST /api/asignaciones
     */
    @PostMapping
    public ResponseEntity<AsignacionDTO> crear(@Valid @RequestBody AsignacionDTO dto) {
        log.info("Solicitud para crear nueva asignación: {}", dto);
        AsignacionDTO nuevaAsignacion = asignacionService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignacion);
    }

    // ==========================
    // ENDPOINTS DE ACTUALIZACIÓN (PUT)
    // ==========================

    /**
     * Actualizar una asignación existente
     * PUT /api/asignaciones/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<AsignacionDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AsignacionDTO dto) {
        log.info("Solicitud para actualizar asignación con ID: {}", id);
        AsignacionDTO asignacionActualizada = asignacionService.actualizar(id, dto);
        return ResponseEntity.ok(asignacionActualizada);
    }

    // ==========================
    // ENDPOINTS DE DEVOLUCIÓN (PATCH)
    // ==========================

    /**
     * Devolver una asignación (marcar como DEVUELTA)
     * PATCH /api/asignaciones/{id}/devolver
     */
    @PatchMapping("/{id}/devolver")
    public ResponseEntity<AsignacionDTO> devolver(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDevolucion,
            @RequestParam(required = false) String observaciones) {
        log.info("Solicitud para devolver asignación con ID: {}", id);
        AsignacionDTO asignacionDevuelta = asignacionService.devolver(id, fechaDevolucion, observaciones);
        return ResponseEntity.ok(asignacionDevuelta);
    }

    /**
     * Devolver una asignación (con cuerpo JSON)
     * PATCH /api/asignaciones/{id}/devolver-con-json
     */
    @PatchMapping("/{id}/devolver-con-json")
    public ResponseEntity<AsignacionDTO> devolverConJson(
            @PathVariable Long id,
            @RequestBody DevolucionRequest request) {
        log.info("Solicitud para devolver asignación con ID: {}", id);
        AsignacionDTO asignacionDevuelta = asignacionService.devolver(
                id,
                request.getFechaDevolucion(),
                request.getObservaciones()
        );
        return ResponseEntity.ok(asignacionDevuelta);
    }

    // ==========================
    // ENDPOINTS DE ELIMINACIÓN (DELETE)
    // ==========================

    /**
     * Eliminar lógicamente una asignación
     * DELETE /api/asignaciones/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Solicitud para eliminar asignación con ID: {}", id);
        asignacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================
    // CLASES INTERNAS PARA REQUESTS
    // ==========================

    /**
     * DTO para la solicitud de devolución con JSON
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DevolucionRequest {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate fechaDevolucion;
        private String observaciones;
    }
}