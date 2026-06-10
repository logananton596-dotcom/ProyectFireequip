package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.AsignacionEquipoDTO;
import com.fireequipmanager.backend.service.AsignacionEquipoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
@CrossOrigin(origins = "*") // Ajusta según la URL y puertos de tu frontend en desarrollo/producción
public class AsignacionEquipoController {

    private final AsignacionEquipoService asignacionService;

    // Inyección por constructor alineado a tu arquitectura
    public AsignacionEquipoController(AsignacionEquipoService asignacionService) {
        this.asignacionService = asignacionService;
    }

    // Listar todas las asignaciones (para el tablero general del inventario)
    @GetMapping
    public ResponseEntity<List<AsignacionEquipoDTO>> listarTodas() {
        return ResponseEntity.ok(asignacionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignacionEquipoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionService.buscarPorId(id));
    }

    // Historial por Bombero: GET /api/asignaciones/bombero/{id}
    @GetMapping("/bombero/{bomberoId}")
    public ResponseEntity<List<AsignacionEquipoDTO>> listarPorBombero(@PathVariable Long bomberoId) {
        return ResponseEntity.ok(asignacionService.listarPorBombero(bomberoId));
    }

    // Historial por Equipo: GET /api/asignaciones/equipo/{id}
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<AsignacionEquipoDTO>> listarPorEquipo(@PathVariable Long equipoId) {
        return ResponseEntity.ok(asignacionService.listarPorEquipo(equipoId));
    }

    @PostMapping
    public ResponseEntity<AsignacionEquipoDTO> crearAsignacion(@Valid @RequestBody AsignacionEquipoDTO dto) {
        AsignacionEquipoDTO nuevaAsignacion = asignacionService.crearAsignacion(dto);
        return new ResponseEntity<>(nuevaAsignacion, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsignacion(@PathVariable Long id) {
        asignacionService.eliminarAsignacion(id);
        return ResponseEntity.noContent().build();
    }
}
