package com.fireequipmanager.backend.controller;


import com.fireequipmanager.backend.dto.BomberoDTO;
import com.fireequipmanager.backend.service.BomberoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bomberos")
@CrossOrigin(origins = "*") // Ajusta esto según las políticas de seguridad de tu frontend
public class BomberoController {

    private final BomberoService bomberoService;

    // Inyección por constructor alineado a tu arquitectura
    public BomberoController(BomberoService bomberoService) {
        this.bomberoService = bomberoService;
    }

    // Listar todos los bomberos (útil para la tabla de administración de personal)
    @GetMapping
    public ResponseEntity<List<BomberoDTO>> listarTodos() {
        return ResponseEntity.ok(bomberoService.listarTodos());
    }

    // Listar solo bomberos activos (el endpoint clave para tu desplegable del frontend)
    @GetMapping("/activos")
    public ResponseEntity<List<BomberoDTO>> listarActivos() {
        return ResponseEntity.ok(bomberoService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BomberoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bomberoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<BomberoDTO> crearBombero(@Valid @RequestBody BomberoDTO bomberoDTO) {
        BomberoDTO nuevoBombero = bomberoService.crearBombero(bomberoDTO);
        return new ResponseEntity<>(nuevoBombero, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BomberoDTO> actualizarBombero(
            @PathVariable Long id, 
            @Valid @RequestBody BomberoDTO bomberoDTO) {
        return ResponseEntity.ok(bomberoService.actualizarBombero(id, bomberoDTO));
    }

    // Endpoint para desactivar/activar rápidamente sin enviar todo el objeto completo
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoActivo(
            @PathVariable Long id, 
            @RequestParam boolean activo) {
        bomberoService.cambiarEstadoActivo(id, activo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDefinitivo(@PathVariable Long id) {
        bomberoService.eliminarDefinitivo(id);
        return ResponseEntity.noContent().build();
    }
}