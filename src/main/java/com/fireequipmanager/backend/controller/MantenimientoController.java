package com.fireequipmanager.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*") // Importante para la conexión con el Frontend
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    public MantenimientoController(MantenimientoService mantenimientoService) {
        this.mantenimientoService = mantenimientoService;
    }

    // REGISTRAR INGRESO A MANTENIMIENTO
    @PostMapping
    public ResponseEntity<Mantenimiento> registrar(@RequestBody Mantenimiento mantenimiento) {
        return new ResponseEntity<>(
                mantenimientoService.registrarMantenimiento(mantenimiento), 
                HttpStatus.CREATED
        );
    }

    // LISTAR HISTORIAL DE MANTENIMIENTOS POR EQUIPO
    @GetMapping("/equipo/{id}")
    public ResponseEntity<List<Mantenimiento>> obtenerPorEquipo(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.obtenerPorEquipo(id));
    }

    // FINALIZAR MANTENIMIENTO Y PROGRAMAR PRÓXIMO
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Mantenimiento> finalizar(
            @PathVariable Long id, 
            @RequestParam(defaultValue = "6") int meses // Valor por defecto de 6 meses
    ) {
        return ResponseEntity.ok(mantenimientoService.finalizarMantenimiento(id, meses));
    }
}
