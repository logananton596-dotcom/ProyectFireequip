package com.fireequipmanager.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fireequipmanager.backend.exception.BusinessException;

import com.fireequipmanager.backend.dto.AsignacionDTO;
import com.fireequipmanager.backend.model.Asignacion;
import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.model.UsoEmergencia;
import com.fireequipmanager.backend.repository.AsignacionRepository;
import com.fireequipmanager.backend.repository.EquipoRepository;
import com.fireequipmanager.backend.repository.UsoEmergenciaRepository;

import java.time.LocalDateTime;
import java.util.List;
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


    public AsignacionDTO asignarEquipo(AsignacionDTO dto ) {

        // 1. Buscar el equipo
        Equipo equipo = equipoRepository.findById(dto.getEquipoId())
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));

        // 2. RN-05: Validar disponibilidad por estado
        String nombreEstado = equipo.getEstadoEquipo().getNombre();
        if (nombreEstado.equalsIgnoreCase("EN_MANTENIMIENTO") || 
            nombreEstado.equalsIgnoreCase("DADO_BAJA") || 
            nombreEstado.equalsIgnoreCase("DADO_DE_BAJA")) {
            throw new BusinessException("No se puede asignar: El equipo está " + nombreEstado);
        }

        // 3. NUEVA RN: Validar si el tipo de equipo es permitido para este uso de emergencia
        // Nota: Asegúrate de agregar 'private Long usoEmergenciaId;' en tu AsignacionDTO si deseas capturarlo desde la petición principal.
        if (dto.getFechaFin() != null) { // Usamos el campo para verificar lógica condicional o extendemos el DTO si es necesario
            // Si tu frontend maneja flujos donde se asocia una emergencia, puedes mapearlo aquí.
        }

        // 3. NUEVA RN: Validar si el tipo de equipo es permitido para este uso de emergencia
        if (dto.getUsoEmergenciaId() != null) {
            UsoEmergencia uso = usoEmergenciaRepository.findById(dto.getUsoEmergenciaId())
                    .orElseThrow(() -> new BusinessException("Uso de emergencia no válido"));
            
            // Verificamos si el tipo de nuestro equipo está en la lista permitida del UsoEmergencia
            if (!uso.getTiposPermitidos().contains(equipo.getTipoEquipo())) {
                throw new BusinessException("Este equipo no está autorizado para el uso: " + uso.getNombre());
            }
        }

        // 4. Cerrar asignación anterior si existe (el equipo cambia de manos)
        asignacionRepository.findByEquipoIdAndFechaFinIsNull(dto.getEquipoId())
                .ifPresent(asignacionActiva -> {
                    asignacionActiva.setFechaFin(LocalDateTime.now());
                    asignacionRepository.save(asignacionActiva);
                });

        // 5. Crear nueva asignación
        Asignacion nueva = new Asignacion();
        nueva.setEquipo(equipo);
        nueva.setTipoAsignacion(dto.getTipoAsignacion());// Ejem: "VEHICULO", "ESTACION"
        nueva.setDestino(dto.getDestino());
        nueva.setFechaInicio(LocalDateTime.now());

        Asignacion guardada = asignacionRepository.save(nueva);
        return convertirAEntityADto(guardada);
    }

    public List<AsignacionDTO> obtenerHistorialPorEquipo(Long equipoId) {
        return asignacionRepository.findByEquipoId(equipoId).stream()
                .map(this::convertirAEntityADto)
                .toList();
    }
    
    public void finalizarAsignacionActual(Long equipoId) {
        asignacionRepository.findByEquipoIdAndFechaFinIsNull(equipoId)
            .ifPresentOrElse(a -> {
                a.setFechaFin(LocalDateTime.now());
                asignacionRepository.save(a);
            }, () -> {
                throw new BusinessException("No se encontró una asignación activa para el equipo especificado");
            });
    }

   public void registrarSalidaEmergencia(Long equipoId, Long usoId) {
        // 1. Buscar el equipo y el tipo de uso
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));
                
        UsoEmergencia uso = usoEmergenciaRepository.findById(usoId)
                .orElseThrow(() -> new BusinessException("Tipo de uso de emergencia no encontrado"));

        // 2. LA VALIDACIÓN
        // Compara si el tipo del equipo está en la lista de permitidos del uso seleccionado
        if (!uso.getTiposPermitidos().contains(equipo.getTipoEquipo())) {
            throw new BusinessException("El equipo '" + equipo.getNombre() + 
                "' de tipo '" + equipo.getTipoEquipo().getNombre() + 
                "' no está permitido para el uso: " + uso.getNombre());
        }
        // 3. Si pasa la validación, procedes con la lógica (ej. cambiar estado a EN_USO)
        // Nota: Agrega aquí tu lógica para persistir el cambio de estado si cuentas con esa propiedad en el flujo.
    }

    // ==========================================
    // MÉTODO PRIVADO DE MAPEO
    // ==========================================
    private AsignacionDTO convertirAEntityADto(Asignacion asignacion) {
        AsignacionDTO dto = new AsignacionDTO();
        dto.setId(asignacion.getId());
        dto.setTipoAsignacion(asignacion.getTipoAsignacion());
        dto.setDestino(asignacion.getDestino());
        dto.setFechaInicio(asignacion.getFechaInicio());
        dto.setFechaFin(asignacion.getFechaFin());

        if (asignacion.getEquipo() != null) {
            dto.setEquipoId(asignacion.getEquipo().getId());
            dto.setEquipoCodigoInterno(asignacion.getEquipo().getCodigoInterno());
            dto.setEquipoNombre(asignacion.getEquipo().getNombre());
        }
        return dto;
    }

}




