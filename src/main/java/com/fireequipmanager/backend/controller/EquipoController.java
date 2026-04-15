package com.fireequipmanager.backend.controller;

import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.service.EquipoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @PostMapping
    public Equipo crear(@RequestBody Equipo equipo) {
        return equipoService.guardar(equipo);
    }

    @GetMapping
    public List<Equipo> listar() {
        return equipoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Equipo obtenerPorId(@PathVariable Long id) {
        return equipoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Equipo actualizar(@PathVariable Long id, @RequestBody Equipo equipo) {
        return equipoService.actualizar(id, equipo);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        equipoService.eliminar(id);
    }
}