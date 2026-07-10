package com.fireequipmanager.backend.service;


import com.fireequipmanager.backend.dto.AsignacionEquipoDTO;
import com.fireequipmanager.backend.model.AsignacionEquipo;
import com.fireequipmanager.backend.model.Bombero;
import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.model.EstadoEquipo;
import com.fireequipmanager.backend.repository.AsignacionEquipoRepository;
import com.fireequipmanager.backend.repository.BomberoRepository;
import com.fireequipmanager.backend.repository.EquipoRepository;
import com.fireequipmanager.backend.repository.EstadoEquipoRepository;
import com.fireequipmanager.backend.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Transactional
public class AsignacionEquipoService {

    private final AsignacionEquipoRepository asignacionRepository;
    private final EquipoRepository equipoRepository;
    private final BomberoRepository bomberoRepository;
    private final EstadoEquipoRepository estadoEquipoRepository;        

    // Inyección por constructor alineado a tu arquitectura
    public AsignacionEquipoService(AsignacionEquipoRepository asignacionRepository,
                                   EquipoRepository equipoRepository,
                                   BomberoRepository bomberoRepository,
                                   EstadoEquipoRepository estadoEquipoRepository) {
        this.asignacionRepository = asignacionRepository;
        this.equipoRepository = equipoRepository;
        this.bomberoRepository = bomberoRepository;
        this.estadoEquipoRepository = estadoEquipoRepository;
    }

    public List<AsignacionEquipoDTO> listarTodas() {
        return asignacionRepository.findAll().stream()
                .map(this::convertirAEntityADto)
                .toList();
    }

    public AsignacionEquipoDTO buscarPorId(Long id) {
        return asignacionRepository.findById(id)
                .map(this::convertirAEntityADto)
                .orElseThrow(() -> new BusinessException("Asignación no encontrada"));
    }

    // Historial dinámico por Bombero
    public List<AsignacionEquipoDTO> listarPorBombero(Long bomberoId) {
        return asignacionRepository.findByBomberoId(bomberoId).stream()
                .map(this::convertirAEntityADto)
                .toList();
    }

    // Historial dinámico por Equipo
    public List<AsignacionEquipoDTO> listarPorEquipo(Long equipoId) {
        return asignacionRepository.findByEquipoId(equipoId).stream()
                .map(this::convertirAEntityADto)
                .toList();
    }

    public AsignacionEquipoDTO crearAsignacion(AsignacionEquipoDTO dto) {
        // 1. Validar que el equipo exista
        Equipo equipo = equipoRepository.findById(dto.getEquipoId())
                .orElseThrow(() -> new BusinessException("El Equipo especificado no existe"));

        // 2. Validar que el bombero exista
        Bombero bombero = bomberoRepository.findById(dto.getBomberoId())
                .orElseThrow(() -> new BusinessException("El Bombero especificado no existe"));

        // Regra de Negocio: No asignar equipos a bomberos inactivos (de baja)
        if (bombero.getEstado() == null || !"ACTIVO".equalsIgnoreCase(bombero.getEstado().name())) {
            throw new BusinessException("No se puede asignar equipamiento a un bombero que no está en estado ACTIVO. Estado actual: " + bombero.getEstado());
        }

        // Regla de Negocio: Evitar re-asignar un equipo que ya está ocupado (Opcional)
        if (equipo.getEstadoEquipo() != null && equipo.getEstadoEquipo().getNombre().equalsIgnoreCase("ASIGNADO")) {
            throw new BusinessException("Este equipo ya se encuentra asignado a otro bombero");
        }

        // CAMBIAR EL ESTADO DEL EQUIPO A "ASIGNADO"
        EstadoEquipo estadoAsignado = estadoEquipoRepository.findByNombre("ASIGNADO")
                .orElseThrow(() -> new BusinessException("El estado 'ASIGNADO' no está configurado en el sistema"));
        
        
        equipo.setEstadoEquipo(estadoAsignado);
        equipoRepository.save(equipo); // Actualiza el equipo en la BD
        // =========================================================================

        // 3. Mapear y guardar la transacción
        AsignacionEquipo asignacion = new AsignacionEquipo();
        asignacion.setEquipo(equipo);
        asignacion.setBombero(bombero);
        asignacion.setTallaEquipo(dto.getTallaEquipo());
        asignacion.setCaracteristicasEspecificas(dto.getCaracteristicasEspecificas());
        asignacion.setFechaPuestaOperatividad(dto.getFechaPuestaOperatividad());
        asignacion.setFechaCaducidad(dto.getFechaCaducidad());
        asignacion.setEstadoFisicoEntrega(dto.getEstadoFisicoEntrega()); // Carga directa String
        asignacion.setTipoMovimiento(dto.getTipoMovimiento());           // Carga directa String
        
        // Datos del responsable que aprueba
        asignacion.setResponsableNombre(dto.getResponsableNombre());
        asignacion.setResponsableCodigo(dto.getResponsableCodigo());
        asignacion.setResponsableGrado(dto.getResponsableGrado());
        asignacion.setResponsableCargo(dto.getResponsableCargo());
        asignacion.setResponsableTelefono(dto.getResponsableTelefono());

        AsignacionEquipo guardada = asignacionRepository.save(asignacion);
        return convertirAEntityADto(guardada);
    }

    public void eliminarAsignacion(Long id) {
        // 1. Validar que la asignación exista antes de proceder
        AsignacionEquipo asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("La asignación que intenta eliminar no existe"));

        // 2. Recuperar el equipo asociado a esta asignación
        Equipo equipo = asignacion.getEquipo();
        // PROCESO INVERSO: CAMBIAR EL ESTADO DEL EQUIPO A "DISPONIBLE"
        // Buscamos el estado 'DISPONIBLE' en la tabla estado_equipo
        EstadoEquipo estadoDisponible = estadoEquipoRepository.findByNombre("DISPONIBLE")
                .orElseThrow(() -> new BusinessException("El estado 'DISPONIBLE' no está configurado en el sistema"));
        
        // Le asignamos el nuevo estado al equipo
        equipo.setEstadoEquipo(estadoDisponible);
        
        // Guardamos el cambio del equipo en la base de datos
        equipoRepository.save(equipo);
        // 3. Finalmente, procedemos a borrar físicamente el registro de la asignación
        asignacionRepository.delete(asignacion);
    }
    // MAPPER MANUAL: Une los datos de las 3 tablas en un JSON plano para el Front
   private AsignacionEquipoDTO convertirAEntityADto(AsignacionEquipo asignacion) {
    AsignacionEquipoDTO dto = new AsignacionEquipoDTO();
    dto.setId(asignacion.getId());
    // 1. Blindaje Seguro para la Entidad Equipo
    Equipo equipo = asignacion.getEquipo();
    if (equipo != null) {
        dto.setEquipoId(equipo.getId());
        dto.setEquipoCodigoInterno(equipo.getCodigoInterno());
        dto.setEquipoNumeroSerie(equipo.getNumeroSerie());
        dto.setEquipoNombre(equipo.getNombre());
        dto.setEquipoMarca(equipo.getMarca());
        // Evita NullPointerException si el equipo no tiene asignado un tipo
        if (equipo.getTipoEquipo() != null) {
            dto.setTipoEquipoNombre(equipo.getTipoEquipo().getNombre());
        } else {
            dto.setTipoEquipoNombre("Sin Tipo Definido");
        }
    } else {
        dto.setEquipoNombre("Equipo Eliminado/No encontrado");
    }

    // 2. Blindaje Seguro para la Entidad Bombero
    Bombero bombero = asignacion.getBombero();
    if (bombero != null) {
        dto.setBomberoId(bombero.getId());
        dto.setBomberoCodigo(bombero.getCodigoCgbvp()); 
        dto.setBomberoNombre(bombero.getNombre());
        dto.setBomberoGrado(bombero.getGrado() != null ? bombero.getGrado().name() : null);
    } else {
        dto.setBomberoNombre("Bombero No Asignado u Huérfano");
    }

    // 3. Atributos nativos de la entrega
    dto.setTallaEquipo(asignacion.getTallaEquipo());
    dto.setCaracteristicasEspecificas(asignacion.getCaracteristicasEspecificas());
    dto.setFechaPuestaOperatividad(asignacion.getFechaPuestaOperatividad());
    dto.setFechaCaducidad(asignacion.getFechaCaducidad());
    dto.setEstadoFisicoEntrega(asignacion.getEstadoFisicoEntrega());
    dto.setTipoMovimiento(asignacion.getTipoMovimiento());
    
    // Responsable
    dto.setResponsableNombre(asignacion.getResponsableNombre());
    dto.setResponsableCodigo(asignacion.getResponsableCodigo());
    dto.setResponsableGrado(asignacion.getResponsableGrado());
    dto.setResponsableCargo(asignacion.getResponsableCargo());
    dto.setResponsableTelefono(asignacion.getResponsableTelefono());
    
    // Auditoría
    dto.setFechaHoraEntrega(asignacion.getFechaHoraEntrega());

    return dto;
}
}