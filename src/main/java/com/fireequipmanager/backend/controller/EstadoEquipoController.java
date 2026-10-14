package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.service.EstadoEquipoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fireequipmanager.backend.dto.EstadoEquipoDTO;

import java.util.List;

@RestController
@RequestMapping("/api/estados-equipo")
@CrossOrigin(origins = "*")
public class EstadoEquipoController {

    private final EstadoEquipoService service;

    public EstadoEquipoController(EstadoEquipoService service) {
        this.service = service;
    }

    // LISTAR TODOS LOS ESTADOS
    @GetMapping
    public ResponseEntity<List<EstadoEquipoDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // CREAR UN NUEVO ESTADO DE EQUIPO
    // @Valid: Activa la validación del EstadoEquipoDTO antes de procesar el JSON
    @PostMapping
    public ResponseEntity<EstadoEquipoDTO> crear(@Valid @RequestBody EstadoEquipoDTO estadoDTO) {
        return new ResponseEntity<>(service.crear(estadoDTO), HttpStatus.CREATED);
    }

    // BUSCAR ESTADO POR NOMBRE
    // Ejemplo: GET /api/estados/buscar?nombre=OPERATIVO
    @GetMapping("/buscar")
    public ResponseEntity<EstadoEquipoDTO> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(service.buscarPorNombre(nombre));
    }
}