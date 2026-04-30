package com.fireequipmanager.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fireequipmanager.backend.model.Asignacion;
import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.repository.AsignacionRepository;
import com.fireequipmanager.backend.repository.EquipoRepository;

@Service
public class AsignacionService {

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    public Asignacion asignarEquipo(Long equipoId, String tipo, String destino) {

        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // RN-05: validar estado
        String estado = equipo.getEstadoEquipo().getNombre();

        if (estado.equals("En Mantenimiento") || estado.equals("Fuera de Servicio")) {
            throw new RuntimeException("No se puede asignar un equipo en mantenimiento o fuera de servicio");
        }

        // Cerrar asignación anterior si existe
        asignacionRepository.findByEquipoIdAndFechaFinIsNull(equipoId)
                .ifPresent(asignacionActiva -> {
                    asignacionActiva.setFechaFin(LocalDateTime.now());
                    asignacionRepository.save(asignacionActiva);
                });

        // Crear nueva asignación
        Asignacion nueva = new Asignacion();
        nueva.setEquipo(equipo);
        nueva.setTipoAsignacion(tipo);
        nueva.setDestino(destino);
        nueva.setFechaInicio(LocalDateTime.now());
        nueva.setFechaFin(null);

        return asignacionRepository.save(nueva);
    }

    public List<Asignacion> historial(Long equipoId) {
        return asignacionRepository.findByEquipoId(equipoId);
    }
}