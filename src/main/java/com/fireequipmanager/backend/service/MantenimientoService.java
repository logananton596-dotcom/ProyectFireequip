package com.fireequipmanager.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.dto.MantenimientoDTO;
import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.model.EstadoEquipo;
import com.fireequipmanager.backend.model.Mantenimiento;
import com.fireequipmanager.backend.repository.EquipoRepository;
import com.fireequipmanager.backend.repository.EstadoEquipoRepository;
import com.fireequipmanager.backend.repository.MantenimientoRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional 
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final EquipoRepository equipoRepository;
    private final EstadoEquipoRepository estadoRepository;
    public MantenimientoService(MantenimientoRepository mantenimientoRepository,
                                EquipoRepository equipoRepository, 
                                EstadoEquipoRepository estadoRepository) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.equipoRepository = equipoRepository;
        this.estadoRepository = estadoRepository;
    }

 public MantenimientoDTO registrarMantenimiento(MantenimientoDTO dto) {
        // 1. Validar equipo mediante el ID provisto por el DTO
        Equipo equipo = equipoRepository.findById(dto.getEquipoId())
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));

        // 2. Validar que el equipo no esté ya en mantenimiento o dado de baja
        String estadoActual = equipo.getEstadoEquipo().getNombre();
        if (estadoActual.equals("EN_MANTENIMIENTO") || estadoActual.contains("BAJA")) {
            throw new BusinessException("El equipo no está disponible para mantenimiento (Estado: " + estadoActual + ")");
        }

        // 3. Validar tipo de mantenimiento
        if (dto.getTipo() == null || 
           (!dto.getTipo().equalsIgnoreCase("PREVENTIVO") && 
            !dto.getTipo().equalsIgnoreCase("CORRECTIVO"))) {
            throw new BusinessException("El tipo debe ser PREVENTIVO o CORRECTIVO");
        }

        // 4. Mapear datos del DTO a una nueva Entidad
        Mantenimiento mantenimiento = new Mantenimiento();
        mantenimiento.setTipo(dto.getTipo().toUpperCase());
        mantenimiento.setDescripcion(dto.getDescripcion());
        mantenimiento.setResponsable(dto.getResponsable());

        // Configurar fechas
        if (dto.getFecha() == null) {
            mantenimiento.setFecha(LocalDate.now());
        } else {
            mantenimiento.setFecha(dto.getFecha());
        }

        // 5. Cambiar estado del equipo a EN_MANTENIMIENTO
        EstadoEquipo estadoEnMante = estadoRepository.findByNombre("EN_MANTENIMIENTO")
                .orElseThrow(() -> new BusinessException("Estado 'EN_MANTENIMIENTO' no configurado en BD"));
        
        equipo.setEstadoEquipo(estadoEnMante);
        equipoRepository.save(equipo);

        mantenimiento.setEquipo(equipo);
        Mantenimiento guardado = mantenimientoRepository.save(mantenimiento);
        
        return convertirAEntityADto(guardado);
    }

    public MantenimientoDTO finalizarMantenimiento(Long mantenimientoId, int mesesParaProximo) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(mantenimientoId)
                .orElseThrow(() -> new BusinessException("Mantenimiento no encontrado"));

        if (mantenimiento.getFechaFin() != null) {
            throw new BusinessException("Este mantenimiento ya fue cerrado anteriormente");
        }

        LocalDate hoy = LocalDate.now();
        
        // Validación lógica de fechas
        if (hoy.isBefore(mantenimiento.getFecha())) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        mantenimiento.setFechaFin(hoy);
        mantenimiento.setFechaProximo(hoy.plusMonths(mesesParaProximo));

        // Regresar equipo a OPERATIVO
        Equipo equipo = mantenimiento.getEquipo();
        EstadoEquipo operativo = estadoRepository.findByNombre("OPERATIVO")
                .orElseThrow(() -> new BusinessException("Estado 'OPERATIVO' no configurado en BD"));
        
        equipo.setEstadoEquipo(operativo);
        equipoRepository.save(equipo);

        Mantenimiento finalizado = mantenimientoRepository.save(mantenimiento);
        return convertirAEntityADto(finalizado);
    }

    public List<MantenimientoDTO> obtenerPorEquipo(Long equipoId) {
        return mantenimientoRepository.findByEquipoId(equipoId).stream()
                .map(this::convertirAEntityADto)
                .toList();
    }

    // ==========================================
    // MÉTODO PRIVADO DE MAPEO
    // ==========================================
    private MantenimientoDTO convertirAEntityADto(Mantenimiento m) {
        MantenimientoDTO dto = new MantenimientoDTO();
        dto.setId(m.getId());
        dto.setFecha(m.getFecha());
        dto.setTipo(m.getTipo());
        dto.setDescripcion(m.getDescripcion());
        dto.setResponsable(m.getResponsable());
        dto.setFechaProximo(m.getFechaProximo());
        dto.setFechaFin(m.getFechaFin());

        if (m.getEquipo() != null) {
            dto.setEquipoId(m.getEquipo().getId());
            dto.setEquipoCodigoInterno(m.getEquipo().getCodigoInterno());
            dto.setEquipoNombre(m.getEquipo().getNombre());
            dto.setEquipoMarca(m.getEquipo().getMarca());
        }
        return dto;
    }

}