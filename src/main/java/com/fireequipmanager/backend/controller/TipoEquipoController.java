package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.model.TipoEquipo;
import com.fireequipmanager.backend.service.TipoEquipoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos")
@CrossOrigin(origins = "*")
public class TipoEquipoController {

    private final TipoEquipoService service;

    public TipoEquipoController(TipoEquipoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TipoEquipo>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    public ResponseEntity<TipoEquipo> crear(@RequestBody TipoEquipo tipo) {
        return new ResponseEntity<>(service.crear(tipo), HttpStatus.CREATED);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<TipoEquipo> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(service.buscarPorNombre(nombre));
    }
    
}