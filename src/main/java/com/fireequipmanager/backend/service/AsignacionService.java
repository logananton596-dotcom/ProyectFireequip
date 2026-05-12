package com.fireequipmanager.backend.service;
import java.time.LocalDateTime;
import java.util.List;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Asignacion;
import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.model.UsoEmergencia;
import com.fireequipmanager.backend.repository.AsignacionRepository;
import com.fireequipmanager.backend.repository.EquipoRepository;
import com.fireequipmanager.backend.repository.UsoEmergenciaRepository;

@Service
@Transactional
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final EquipoRepository equipoRepository;
    private final UsoEmergenciaRepository usoEmergenciaRepository;

    // Inyección por constructor (Arquitectura limpia)
    public AsignacionService(AsignacionRepository asignacionRepository, 
                             EquipoRepository equipoRepository,
                             UsoEmergenciaRepository usoEmergenciaRepository) {
        this.asignacionRepository = asignacionRepository;
        this.equipoRepository = equipoRepository;
        this.usoEmergenciaRepository = usoEmergenciaRepository;
    }


    public Asignacion asignarEquipo(Long equipoId, String tipo, String destino, Long usoEmergenciaId) {

        // 1. Buscar el equipo
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));

        // 2. RN-05: Validar disponibilidad por estado
        String nombreEstado = equipo.getEstadoEquipo().getNombre();
        if (nombreEstado.equals("EN_MANTENIMIENTO") || 
            nombreEstado.equals("DADO_BAJA") || 
            nombreEstado.equals("DADO_DE_BAJA")) {
            throw new BusinessException("No se puede asignar: El equipo está " + nombreEstado);
        }

        // 3. NUEVA RN: Validar si el tipo de equipo es permitido para este uso de emergencia
        if (usoEmergenciaId != null) {
            UsoEmergencia uso = usoEmergenciaRepository.findById(usoEmergenciaId)
                    .orElseThrow(() -> new BusinessException("Uso de emergencia no válido"));
            
            // Verificamos si el tipo de nuestro equipo está en la lista permitida del UsoEmergencia
            if (!uso.getTiposPermitidos().contains(equipo.getTipoEquipo())) {
                throw new BusinessException("Este equipo no está autorizado para el uso: " + uso.getNombre());
            }
        }

        // 4. Cerrar asignación anterior si existe (el equipo cambia de manos)
        asignacionRepository.findByEquipoIdAndFechaFinIsNull(equipoId)
                .ifPresent(asignacionActiva -> {
                    asignacionActiva.setFechaFin(LocalDateTime.now());
                    asignacionRepository.save(asignacionActiva);
                });

        // 5. Crear nueva asignación
        Asignacion nueva = new Asignacion();
        nueva.setEquipo(equipo);
        nueva.setTipoAsignacion(tipo); // Ejem: "VEHICULO", "ESTACION"
        nueva.setDestino(destino);
        nueva.setFechaInicio(LocalDateTime.now());

        return asignacionRepository.save(nueva);
    }

    public List<Asignacion> obtenerHistorialPorEquipo(Long equipoId) {
        return asignacionRepository.findByEquipoId(equipoId);
    }
    
    public void finalizarAsignacionActual(Long equipoId) {
        asignacionRepository.findByEquipoIdAndFechaFinIsNull(equipoId)
            .ifPresent(a -> {
                a.setFechaFin(LocalDateTime.now());
                asignacionRepository.save(a);
            });
    }

    
    public void registrarSalidaEmergencia(Long equipoId, Long usoId) {
        // 1. Buscar el equipo y el tipo de uso
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));
                
        UsoEmergencia uso = usoEmergenciaRepository.findById(usoId)
                .orElseThrow(() -> new BusinessException("Tipo de uso de emergencia no encontrado"));
        // 2. LA VALIDACIÓN (Aquí es donde la incorporas)
        // Compara si el tipo del equipo está en la lista de permitidos del uso seleccionado
        if (!uso.getTiposPermitidos().contains(equipo.getTipoEquipo())) {
            throw new BusinessException("El equipo '" + equipo.getNombre() + 
                "' de tipo '" + equipo.getTipoEquipo().getNombre() + 
                "' no está permitido para el uso: " + uso.getNombre());
        }
        // 3. Si pasa la validación, procedes con la lógica (ej. cambiar estado a EN_USO)
    }
}




