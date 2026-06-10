package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.dto.UsoEmergenciaDTO;
import com.fireequipmanager.backend.service.UsoEmergenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usos-emergencia")
@CrossOrigin(origins = "*") // Permite la comunicación con el Frontend
public class UsoEmergenciaController {

    private final UsoEmergenciaService usoEmergenciaService;

    public UsoEmergenciaController(UsoEmergenciaService usoEmergenciaService) {
        this.usoEmergenciaService = usoEmergenciaService;
    }

    // LISTAR TODOS LOS USOS DE EMERGENCIA
    @GetMapping
    public ResponseEntity<List<UsoEmergenciaDTO>> listarTodos() {
        return ResponseEntity.ok(usoEmergenciaService.listarTodos());
    }

    // OBTENER UN USO DE EMERGENCIA POR SU ID
    @GetMapping("/{id}")
    public ResponseEntity<UsoEmergenciaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usoEmergenciaService.buscarPorId(id));
    }

    // REGISTRAR UN NUEVO USO DE EMERGENCIA (Con sus tipos permitidos)
    // @Valid: Activa las validaciones del DTO (@NotBlank y @NotEmpty para la lista de IDs)
    @PostMapping
    public ResponseEntity<UsoEmergenciaDTO> crear(@Valid @RequestBody UsoEmergenciaDTO dto) {
        return new ResponseEntity<>(usoEmergenciaService.crear(dto), HttpStatus.CREATED);
    }

    // ELIMINAR UN REGISTRO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usoEmergenciaService.eliminar(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }
}