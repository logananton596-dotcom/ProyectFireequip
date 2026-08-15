package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.EppDTO;
import com.fireequipmanager.backend.service.EppService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;
import java.util.List;

@RestController
@RequestMapping("/api/epps")
@CrossOrigin(origins = "*")
public class EppController {

    private final EppService eppService;

    // Inyección por constructor
    public EppController(EppService eppService) {
        this.eppService = eppService;
    }

    // Lista todos los EPP
    @GetMapping
    public ResponseEntity<List<EppDTO>> listarTodos() {

        return ResponseEntity.ok(
                eppService.listarTodos()
        );

    }

    // Busca un EPP por ID
    @GetMapping("/{id}")
    public ResponseEntity<EppDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
               eppService.buscarPorId(Objects.requireNonNull(id, "El ID no puede ser nulo"))
        );

    }

    // Registra un nuevo EPP
    @PostMapping
    public ResponseEntity<EppDTO> crearEpp(
            @Valid @RequestBody EppDTO dto) {

        EppDTO response = eppService.crearEpp(Objects.requireNonNull(dto, "Los datos del EPP no pueden ser nulos"));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Actualiza un EPP
    @PutMapping("/{id}")
    public ResponseEntity<EppDTO> actualizarEpp(
            @PathVariable Long id,
            @Valid @RequestBody EppDTO dto) {

        return ResponseEntity.ok(
                eppService.actualizarEpp(Objects.requireNonNull(id, "El ID no puede ser nulo"), Objects.requireNonNull(dto, "Los datos del EPP no pueden ser nulos"))
        );

    }

    // Elimina un EPP
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        eppService.eliminar(Objects.requireNonNull(id, "El ID no puede ser nulo"));

        return ResponseEntity.noContent().build();

    }

}