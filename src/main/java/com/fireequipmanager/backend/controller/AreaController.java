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

    // Inyección por constructor alineado a tu arquitectura
    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping
    public ResponseEntity<List<AreaDTO>> listarTodas() {
        return ResponseEntity.ok(areaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(areaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AreaDTO> crearArea(@Valid @RequestBody AreaDTO areaDTO) {
        AreaDTO nuevaArea = areaService.crearArea(areaDTO);
        return new ResponseEntity<>(nuevaArea, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaDTO> actualizarArea(
            @PathVariable Long id, 
            @Valid @RequestBody AreaDTO areaDTO) {
        return ResponseEntity.ok(areaService.actualizarArea(id, areaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        areaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}