package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.EquipoDTO;
import com.fireequipmanager.backend.service.EquipoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*")
public class EquipoController {

    private final EquipoService equipoService;

    // Inyección por constructor
    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    // Lista todos los equipos
    @GetMapping
    public ResponseEntity<List<EquipoDTO>> listarTodos() {
        return ResponseEntity.ok(equipoService.listarTodos());
    }

    // Busca un equipo por ID
    @SuppressWarnings("null") 
    @GetMapping("/{id}")
    public ResponseEntity<EquipoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(equipoService.buscarPorId(id));
    }

    // Registra un nuevo equipo
    @SuppressWarnings("null") 
    @PostMapping
    public ResponseEntity<EquipoDTO> crearEquipo(
            @Valid @RequestBody EquipoDTO equipoDTO) {

        EquipoDTO nuevoEquipo = equipoService.crearEquipo(equipoDTO);

        return new ResponseEntity<>(nuevoEquipo, HttpStatus.CREATED);
    }

    // Actualiza un equipo existente
    @SuppressWarnings("null") 
    @PutMapping("/{id}")
    public ResponseEntity<EquipoDTO> actualizarEquipo(
            @PathVariable Long id,
            @Valid @RequestBody EquipoDTO equipoDTO) {

        return ResponseEntity.ok(
                equipoService.actualizarEquipo(id, equipoDTO));
    }

    // Elimina un equipo
    @SuppressWarnings("null") 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        equipoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

}