package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.BomberoDTO;
import com.fireequipmanager.backend.model.enumsBombero.EstadoBombero;
import com.fireequipmanager.backend.service.BomberoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {
    RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS
})

@RequestMapping("/api/bomberos")
public class BomberoController {

    private final BomberoService bomberoService;

    public BomberoController(BomberoService bomberoService) {
        this.bomberoService = bomberoService;
    }

    // Lista todos los bomberos
    @GetMapping
    public ResponseEntity<List<BomberoDTO>> listarTodos() {
        return ResponseEntity.ok(bomberoService.listarTodos());
    }

    // Lista solo bomberos activos
    @GetMapping("/activos")
    public ResponseEntity<List<BomberoDTO>> listarActivos() {
        return ResponseEntity.ok(bomberoService.listarActivos());
    }

    // Busca un bombero por ID
    @GetMapping("/{id}")
    public ResponseEntity<BomberoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bomberoService.buscarPorId(id));
    }

    // Registra un nuevo bombero
    @PostMapping
    public ResponseEntity<BomberoDTO> crearBombero(@Valid @RequestBody BomberoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bomberoService.crearBombero(dto));
    }

    // Actualiza un bombero existente
    @PutMapping("/{id}")
    public ResponseEntity<BomberoDTO> actualizarBombero(
            @PathVariable Long id,
            @Valid @RequestBody BomberoDTO dto) {

        return ResponseEntity.ok(bomberoService.actualizarBombero(id, dto));
    }

    // Cambia el estado administrativo
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoAdministrativo(
            @PathVariable Long id,
            @RequestParam EstadoBombero estado) {

        bomberoService.cambiarEstadoAdministrativo(id, estado);

        return ResponseEntity.noContent().build();
    }

    // Elimina un bombero sin historial
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDefinitivo(@PathVariable Long id) {

        bomberoService.eliminarDefinitivo(id);

        return ResponseEntity.noContent().build();
    }

}