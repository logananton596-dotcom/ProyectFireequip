package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.CasilleroDTO;
import com.fireequipmanager.backend.service.CasilleroService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/casilleros")
@CrossOrigin(origins = "*")
public class CasilleroController {

    private final CasilleroService casilleroService;

    public CasilleroController(CasilleroService casilleroService) {
        this.casilleroService = casilleroService;
    }

    // Lista todos los casilleros
    @GetMapping
    public ResponseEntity<List<CasilleroDTO>> listarTodos() {

        return ResponseEntity.ok(
                casilleroService.listarTodos()
        );
    }

    // Lista únicamente los casilleros activos
    @GetMapping("/activos")
    public ResponseEntity<List<CasilleroDTO>> listarActivos() {

        return ResponseEntity.ok(
                casilleroService.listarActivos()
        );
    }

    // Busca un casillero por ID
    @GetMapping("/{id}")
    public ResponseEntity<CasilleroDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                casilleroService.buscarPorId(Objects.requireNonNull(id, "El ID del casillero no puede ser nulo"))
        );
    }

    // Lista los casilleros de un piso
    @GetMapping("/piso/{piso}")
    public ResponseEntity<List<CasilleroDTO>> listarPorPiso(
            @PathVariable String piso) {

        return ResponseEntity.ok(
                casilleroService.listarPorPiso(piso)
        );
    }

    // Registra un nuevo casillero
    @PostMapping
    public ResponseEntity<CasilleroDTO> crear(
            @Valid @RequestBody CasilleroDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        casilleroService.crear(Objects.requireNonNull(dto, "El DTO del casillero no puede ser nulo"))
                );
    }

    // Actualiza un casillero
    @PutMapping("/{id}")
    public ResponseEntity<CasilleroDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CasilleroDTO dto) {

        return ResponseEntity.ok(
                casilleroService.actualizar(Objects.requireNonNull(id, "El ID del casillero no puede ser nulo"), Objects.requireNonNull(dto, "El DTO del casillero no puede ser nulo"))
        );
    }

    // Activa un casillero
    @PatchMapping("/{id}/activar")
    public ResponseEntity<CasilleroDTO> activar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                casilleroService.activar(Objects.requireNonNull(id, "El ID del casillero no puede ser nulo"))
        );
    }

    // Desactiva un casillero
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<CasilleroDTO> desactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                casilleroService.desactivar(Objects.requireNonNull(id, "El ID del casillero no puede ser nulo"))
        );
    }

    // Elimina un casillero
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        casilleroService.eliminar(Objects.requireNonNull(id, "El ID del casillero no puede ser nulo"));

        return ResponseEntity.noContent().build();
    }
}