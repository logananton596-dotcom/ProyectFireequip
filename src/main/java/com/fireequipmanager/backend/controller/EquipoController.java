package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.service.EquipoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*") // Permite peticiones desde el frontend
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;

    }
    // LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<Equipo>> listar() {
        return ResponseEntity.ok(equipoService.listarTodos());
    }
    
    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Equipo> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(equipoService.buscarPorId(id));
       //return equipoService.buscarPorId(id);
    }

    // CREAR EQUIPO
    @PostMapping
    public ResponseEntity<Equipo> crear(@RequestBody Equipo equipo) {
        return new ResponseEntity<>(equipoService.crearEquipo(equipo), HttpStatus.CREATED);
        //return equipoService.guardar(equipo);
    }

    // ACTUALIZAR EQUIPO
    // Nota: El username se envía como ?username=admin en la URL
    @PutMapping("/{id}")
    public ResponseEntity<Equipo> actualizar(
            @PathVariable Long id, 
            @RequestBody Equipo equipo, 
            @RequestParam String username) {
        return ResponseEntity.ok(equipoService.actualizarEquipo(id, equipo, username));
       // return equipoService.actualizarEquipo(id, equipo, username);
    }

        // DAR DE BAJA
    @PutMapping("/{id}/baja")
    public ResponseEntity<Void> darDeBaja(
            @PathVariable Long id,
            @RequestParam String motivo,
            @RequestParam String autorizado) {
        equipoService.darDeBaja(id, motivo, autorizado);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        equipoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

        // --- ENDPOINTS ESPECIALES ---

    @GetMapping("/alertas-vencimiento")
    public ResponseEntity<List<Equipo>> obtenerAlertas() {
        return ResponseEntity.ok(equipoService.equiposPorVencer());
    }

    @GetMapping("/reporte/estado")
    public ResponseEntity<Map<String, Long>> reporteEstado() {
        return ResponseEntity.ok(equipoService.reportePorEstado());
        //return equipoService.reportePorEstado();
    }

    @GetMapping("/alertas")
    public List<Equipo> alertas() {
        return equipoService.equiposPorVencer();
    }


}

