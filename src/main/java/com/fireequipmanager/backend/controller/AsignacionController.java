package com.fireequipmanager.backend.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fireequipmanager.backend.model.Asignacion;
import com.fireequipmanager.backend.service.AsignacionService;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    private final AsignacionService asignacionService;

    // Inyección por constructor 
    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    // REGISTRAR NUEVA ASIGNACIÓN O SALIDA A EMERGENCIA
    @PostMapping
    public ResponseEntity<Asignacion> asignar(
            @RequestParam Long equipoId,
            @RequestParam String tipo,
            @RequestParam String destino,
            @RequestParam(required = false) Long usoEmergenciaId // Opcional para flexibilidad
        ) {

        return new ResponseEntity<>(
                asignacionService.asignarEquipo(equipoId, tipo, destino, usoEmergenciaId),
                HttpStatus.CREATED
        );
    }

    // OBTENER HISTORIAL DE UN EQUIPO
    @GetMapping("/historial/{equipoId}")
    public ResponseEntity<List<Asignacion>> historial(@PathVariable Long equipoId) {
        return ResponseEntity.ok(asignacionService.obtenerHistorialPorEquipo(equipoId));
    }

    // FINALIZAR ASIGNACIÓN ACTUAL (Regreso de equipo)
    @PutMapping("/finalizar/{equipoId}")
    public ResponseEntity<Void> finalizar(@PathVariable Long equipoId) {
        asignacionService.finalizarAsignacionActual(equipoId);
        return ResponseEntity.noContent().build();
    }
}