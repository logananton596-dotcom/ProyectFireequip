package com.fireequipmanager.backend.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fireequipmanager.backend.model.Asignacion;
import com.fireequipmanager.backend.service.AsignacionService;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    @PostMapping
    public ResponseEntity<Asignacion> asignar(
            @RequestParam Long equipoId,
            @RequestParam String tipo,
            @RequestParam String destino) {

        return ResponseEntity.ok(
                asignacionService.asignarEquipo(equipoId, tipo, destino)
        );
    }

    @GetMapping("/historial/{equipoId}")
    public ResponseEntity<List<Asignacion>> historial(@PathVariable Long equipoId) {
        return ResponseEntity.ok(asignacionService.historial(equipoId));
    }
}