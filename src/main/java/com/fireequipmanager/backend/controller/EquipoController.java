package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.EquipoDTO;
import com.fireequipmanager.backend.dto.EquipoHistorialDTO;
import com.fireequipmanager.backend.service.EquipoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*") // Permite peticiones desde el frontend
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;

    }
    // LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<EquipoDTO>> listar() {
        return ResponseEntity.ok(equipoService.listarTodos());
    }
    
    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<EquipoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(equipoService.buscarPorId(id));
    }

    // CREAR EQUIPO
    // @Valid: Activa las validaciones del EquipoDTO (@NotBlank, @NotNull, etc.)
    @PostMapping
    public ResponseEntity<EquipoDTO> crear(@Valid @RequestBody EquipoDTO equipoDTO) {
        return new ResponseEntity<>(equipoService.crearEquipo(equipoDTO), HttpStatus.CREATED);
    }

    // ACTUALIZAR EQUIPO
    // Nota: El username se envía como ?username=admin en la URL
    @PutMapping("/{id}")
    public ResponseEntity<EquipoDTO> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody EquipoDTO equipoDTO, 
            @RequestParam String username) {
        return ResponseEntity.ok(equipoService.actualizarEquipo(id, equipoDTO, username));
    }

    // DAR DE BAJA
    @PutMapping("/{id}/baja")
    public ResponseEntity<Void> darDeBaja(
            @PathVariable Long id,
            @RequestParam String motivo,
            @RequestParam String autorizado) {
        equipoService.darDeBaja(id, motivo, autorizado);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        equipoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

        // --- ENDPOINTS ESPECIALES ---

    @GetMapping("/alertas-vencimiento")
    public ResponseEntity<List<EquipoDTO>> obtenerAlertas() {
        return ResponseEntity.ok(equipoService.equiposPorVencer());
    }

    @GetMapping("/reporte/estado")
    public ResponseEntity<Map<String, Long>> reporteEstado() {
        return ResponseEntity.ok(equipoService.reportePorEstado());
    }

    // Consolidado para retornar el DTO correcto en tus alertas alternativas
    @GetMapping("/alertas")
    public ResponseEntity<List<EquipoDTO>> alertas() {
        return ResponseEntity.ok(equipoService.equiposPorVencer());
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<EquipoHistorialDTO>> verHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(equipoService.obtenerHistorial(id));
    }

    @GetMapping("/area/{areaId}")
    public ResponseEntity<List<EquipoDTO>> listarPorArea(@PathVariable Long areaId) {
        return ResponseEntity.ok(equipoService.listarPorArea(areaId));
    }
}

