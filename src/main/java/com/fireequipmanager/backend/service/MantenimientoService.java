package com.fireequipmanager.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
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

        // 1. Validar que el equipo exista
        Equipo equipo = equipoRepository.findById(mantenimiento.getEquipo().getId())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // 2. Validar tipo 
        if (mantenimiento.getTipo() == null ||
            (!mantenimiento.getTipo().equals("PREVENTIVO") &&
             !mantenimiento.getTipo().equals("CORRECTIVO"))) {

            throw new RuntimeException("Tipo de mantenimiento inválido");
        }

        // 3. Validar fecha
        if (mantenimiento.getFecha() == null) {
            mantenimiento.setFecha(LocalDate.now());
        }


        // 3. CAMBIO CLAVE: Buscar el estado real en la BD
        // En lugar de renombrar el estado actual del equipo, le asignamos uno nuevo
        EstadoEquipo estadoEnMantenimiento = 
                    estadoRepository.findByNombre("EN_MANTENIMIENTO")
                .orElseThrow(() -> new RuntimeException
                ("El estado 'EN_MANTENIMIENTO' no existe en la BD"));
        // Asignamos el objeto estado completo al equipo
        equipo.setEstadoEquipo(estadoEnMantenimiento);

        // 4. Guardar cambios
        equipoRepository.save(equipo);
        // 5. Asociar equipo al mantenimiento
        mantenimiento.setEquipo(equipo);

        return mantenimientoRepository.save(mantenimiento);

    }


    public Mantenimiento finalizarMantenimiento(Long mantenimientoId, int mesesParaProximo) {
        // 1. Buscar el mantenimiento
        Mantenimiento mantenimiento = mantenimientoRepository.findById(mantenimientoId)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));

        // 2. Verificar si ya tiene fechaFin (si ya está cerrado)
        if (mantenimiento.getFechaFin() != null) {
            throw new RuntimeException("Este mantenimiento ya fue finalizado");
        }

        // 3. Registrar fin y calcular próximo mantenimiento
        LocalDate hoy = LocalDate.now();
        mantenimiento.setFechaFin(hoy); // Marcamos el cierre
        
        // Calculamos la fecha sugerida para el siguiente mantenimiento
        mantenimiento.setFechaProximo(hoy.plusMonths(mesesParaProximo));

        // 4. Regresar el equipo a estado OPERATIVO
        Equipo equipo = mantenimiento.getEquipo();
        EstadoEquipo operativo = estadoRepository.findByNombre("OPERATIVO")
                .orElseThrow(() -> new RuntimeException("Estado OPERATIVO no encontrado"));
        
        equipo.setEstadoEquipo(operativo);

        // 5. Guardar todo
        equipoRepository.save(equipo);
        return mantenimientoRepository.save(mantenimiento);
    }

    public List<Mantenimiento> obtenerPorEquipo(Long equipoId) {
        return mantenimientoRepository.findByEquipoId(equipoId);
    }
}