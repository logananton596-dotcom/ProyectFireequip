package com.fireequipmanager.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.fireequipmanager.backend.service.MantenimientoService;
import com.fireequipmanager.backend.model.Mantenimiento;

@RestController
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    public MantenimientoController(MantenimientoService mantenimientoService) {
        this.mantenimientoService = mantenimientoService;
    }

    @PostMapping
    public ResponseEntity<Mantenimiento> crear(@RequestBody Mantenimiento mantenimiento) {
        return ResponseEntity.ok(
            // Llamamos al servicio para guardar
                mantenimientoService.registrarMantenimiento(mantenimiento)
        );
    }

    @GetMapping("/equipo/{id}")
    public ResponseEntity<List<Mantenimiento>> obtenerPorEquipo(@PathVariable Long id) {
        return ResponseEntity.ok(
            // Retorna la lista de mantenimientos de un equipo específico
                mantenimientoService.obtenerPorEquipo(id)
        );
    }

      // Nuevo: Endpoint para finalizar el mantenimiento
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Mantenimiento> finalizar(
            @PathVariable Long id, 
            @RequestParam int meses // Ejemplo: /api/mantenimientos/1/finalizar?meses=6
    ) {
        return ResponseEntity.ok(mantenimientoService.finalizarMantenimiento(id, meses));
    }
}
