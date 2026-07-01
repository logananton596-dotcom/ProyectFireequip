package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.TipoEquipoDTO;
import com.fireequipmanager.backend.service.TipoEquipoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-equipo")
@CrossOrigin(origins = "*")
public class TipoEquipoController {

    private final TipoEquipoService service;

    public TipoEquipoController(TipoEquipoService service) {
        this.service = service;
    }

    // LISTAR TODOS LOS TIPOS DE EQUIPO
    @GetMapping
    public ResponseEntity<List<TipoEquipoDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // CREAR UN NUEVO TIPO DE EQUIPO
    // @Valid intercepta el JSON y valida las propiedades del TipoEquipoDTO antes de pasarlo al servicio
    @PostMapping
    public ResponseEntity<TipoEquipoDTO> crear(@Valid @RequestBody TipoEquipoDTO tipoDTO) {
        return new ResponseEntity<>(service.crear(tipoDTO), HttpStatus.CREATED);
    }
    
    // BUSCAR TIPO DE EQUIPO POR NOMBRE
    // Ejemplo: GET /api/tipos/buscar?nombre=EXTINTOR
    @GetMapping("/buscar")
    public ResponseEntity<TipoEquipoDTO> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(service.buscarPorNombre(nombre));
    }
}