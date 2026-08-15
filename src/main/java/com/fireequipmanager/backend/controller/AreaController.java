package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.AreaDTO;
import com.fireequipmanager.backend.service.AreaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
@CrossOrigin(origins = "*") // Ajusta esto según los permisos de tu frontend
public class AreaController {

    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    // Lista todas las áreas
    @GetMapping
    public ResponseEntity<List<AreaDTO>> listarTodas() {
        return ResponseEntity.ok(areaService.listarTodas());
    }

    // Busca un área por ID
    @SuppressWarnings("null") 
    @GetMapping("/{id}")
    public ResponseEntity<AreaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(areaService.buscarPorId(id));
    }

    // Registra una nueva área
    @SuppressWarnings("null") 
    @PostMapping
    public ResponseEntity<AreaDTO> crearArea(@Valid @RequestBody AreaDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(areaService.crearArea(dto));
    }

    // Actualiza un área
    @SuppressWarnings("null") 
    @PutMapping("/{id}")
    public ResponseEntity<AreaDTO> actualizarArea(
            @PathVariable Long id,
            @Valid @RequestBody AreaDTO dto) {

        return ResponseEntity.ok(areaService.actualizarArea(id, dto));
    }

    // Elimina un área
    @SuppressWarnings("null") 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        areaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}