package com.fireequipmanager.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fireequipmanager.backend.exception.BusinessException;
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



    public Mantenimiento registrarMantenimiento(Mantenimiento mantenimiento) {
        // 1. Validar equipo
        Equipo equipo = equipoRepository.findById(mantenimiento.getEquipo().getId())
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));

        // 2. Validar que el equipo no esté ya en mantenimiento o dado de baja
        String estadoActual = equipo.getEstadoEquipo().getNombre();
        if (estadoActual.equals("EN_MANTENIMIENTO") || estadoActual.contains("BAJA")) {
            throw new BusinessException("El equipo no está disponible para mantenimiento (Estado: " + estadoActual + ")");
        }

        // 3. Validar tipo de mantenimiento
        if (mantenimiento.getTipo() == null || 
           (!mantenimiento.getTipo().equalsIgnoreCase("PREVENTIVO") && 
            !mantenimiento.getTipo().equalsIgnoreCase("CORRECTIVO"))) {
            throw new BusinessException("El tipo debe ser PREVENTIVO o CORRECTIVO");
        }

        // 4. Configurar fechas
        if (mantenimiento.getFecha() == null) {
            mantenimiento.setFecha(LocalDate.now());
        }

        // 5. Cambiar estado del equipo a EN_MANTENIMIENTO
        EstadoEquipo estadoEnMante = estadoRepository.findByNombre("EN_MANTENIMIENTO")
                .orElseThrow(() -> new BusinessException("Estado 'EN_MANTENIMIENTO' no configurado en BD"));
        
        equipo.setEstadoEquipo(estadoEnMante);
        equipoRepository.save(equipo);

        mantenimiento.setEquipo(equipo);
        return mantenimientoRepository.save(mantenimiento);
    }

    public Mantenimiento finalizarMantenimiento(Long mantenimientoId, int mesesParaProximo) {
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

        return mantenimientoRepository.save(mantenimiento);
    }

    public List<Mantenimiento> obtenerPorEquipo(Long equipoId) {
        return mantenimientoRepository.findByEquipoId(equipoId);
    }
}