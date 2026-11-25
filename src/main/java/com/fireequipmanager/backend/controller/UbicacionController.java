package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.UbicacionDTO;
import com.fireequipmanager.backend.model.enumsUbicacion.TipoUbicacion;
import com.fireequipmanager.backend.service.UbicacionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
@CrossOrigin(origins = "*")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    // Listar todas
    @GetMapping
    public ResponseEntity<List<UbicacionDTO>> listarTodas() {
        return ResponseEntity.ok(ubicacionService.listarTodas());
    }

    // Listar activas
    @GetMapping("/activas")
    public ResponseEntity<List<UbicacionDTO>> listarActivas() {
        return ResponseEntity.ok(ubicacionService.listarActivas());
    }

    // Listar por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<UbicacionDTO>> listarPorTipo(@PathVariable TipoUbicacion tipo) {
        return ResponseEntity.ok(ubicacionService.listarPorTipo(tipo));
    }

    // Listar raíces (sin padre)
    @GetMapping("/raices")
    public ResponseEntity<List<UbicacionDTO>> listarRaices() {
        return ResponseEntity.ok(ubicacionService.listarRaices());
    }

    // Listar hijas de una ubicación
    @GetMapping("/{id}/hijas")
    public ResponseEntity<List<UbicacionDTO>> listarHijas(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.listarHijas(id));
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<UbicacionDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.buscarPorId(id));
    }

    // Crear
    @PostMapping
    public ResponseEntity<UbicacionDTO> crear(@Valid @RequestBody UbicacionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ubicacionService.crear(dto));
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<UbicacionDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UbicacionDTO dto) {
        return ResponseEntity.ok(ubicacionService.actualizar(id, dto));
    }

    // Activar / desactivar
    @PatchMapping("/{id}/activa")
    public ResponseEntity<UbicacionDTO> cambiarActiva(
            @PathVariable Long id,
            @RequestParam boolean activa) {
        return ResponseEntity.ok(ubicacionService.cambiarActiva(id, activa));
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ubicacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}