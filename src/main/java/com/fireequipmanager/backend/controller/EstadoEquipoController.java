package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.model.EstadoEquipo;
import com.fireequipmanager.backend.service.EstadoEquipoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estados")
@CrossOrigin(origins = "*")
public class EstadoEquipoController {

    private final EstadoEquipoService service;

    public EstadoEquipoController(EstadoEquipoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EstadoEquipo>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    public ResponseEntity<EstadoEquipo> crear(@RequestBody EstadoEquipo estado) {
        return new ResponseEntity<>(service.crear(estado), HttpStatus.CREATED);
    }
        @GetMapping("/buscar")
    public ResponseEntity<EstadoEquipo> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(service.buscarPorNombre(nombre));
    }

}