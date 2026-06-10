package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.dto.EquipoDTO;
import com.fireequipmanager.backend.dto.EquipoHistorialDTO;
import com.fireequipmanager.backend.model.Area;
import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.model.EquipoHistorial;
import com.fireequipmanager.backend.model.EstadoEquipo;
import com.fireequipmanager.backend.model.TipoEquipo;
import com.fireequipmanager.backend.repository.AreaRepository;
import com.fireequipmanager.backend.repository.EquipoHistorialRepository;
import com.fireequipmanager.backend.repository.EquipoRepository;
import com.fireequipmanager.backend.repository.EstadoEquipoRepository;
import com.fireequipmanager.backend.repository.TipoEquipoRepository;
import com.fireequipmanager.backend.exception.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final EquipoHistorialRepository historialRepository; 
    private final EstadoEquipoRepository estadoEquipoRepository;
    private final TipoEquipoRepository tipoEquipoRepository;
    private final AreaRepository areaRepository; 
    
    public EquipoService(EquipoRepository equipoRepository, 
                         EquipoHistorialRepository historialRepository,
                         EstadoEquipoRepository estadoEquipoRepository,
                         TipoEquipoRepository tipoEquipoRepository,
                         AreaRepository areaRepository) {
        this.equipoRepository = equipoRepository;
        this.historialRepository = historialRepository;
        this.estadoEquipoRepository = estadoEquipoRepository;
        this.tipoEquipoRepository = tipoEquipoRepository;
        this.areaRepository = areaRepository;
    }

    public List<EquipoDTO> listarTodos() {
        return equipoRepository.findAll().stream()
                .map(this::convertirAEntityADto)
                .toList();
    }

    public EquipoDTO buscarPorId(Long id) {
        return equipoRepository.findById(id)
                .map(this::convertirAEntityADto)
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));
    }

    public void eliminar(Long id) {
        if (!equipoRepository.existsById(id)) {
            throw new BusinessException("El equipo a eliminar no existe");
        }
        equipoRepository.deleteById(id);
    }

    public List<EquipoDTO> equiposPorVencer() {
        return equipoRepository.findAll().stream()
                .filter(e -> {
                    if (e.getFechaCompra() == null || e.getVidaUtilAnios() == null) return false;

                    LocalDate fechaVencimiento = e.getFechaCompra()
                            .plusYears(e.getVidaUtilAnios());

                    return fechaVencimiento.minusDays(30).isBefore(LocalDate.now());
                })
                .map(this::convertirAEntityADto)
                .toList();
    }

    public EquipoDTO crearEquipo(EquipoDTO equipoDTO) {
        // RN-01: numeroSerie único
        if (equipoRepository.existsByNumeroSerie(equipoDTO.getNumeroSerie())) {
            throw new BusinessException("El número de serie ya existe");
        }
        // Buscar relaciones obligatorias en la BD
        TipoEquipo tipo = tipoEquipoRepository.findById(equipoDTO.getTipoEquipoId())
                .orElseThrow(() -> new BusinessException("El Tipo de Equipo especificado no existe"));

        EstadoEquipo estado = estadoEquipoRepository.findByNombre(equipoDTO.getEstadoEquipoNombre())
                .orElseThrow(() -> new BusinessException("El Estado de Equipo especificado no existe"));

        Area area = areaRepository.findById(equipoDTO.getAreaId())
            .orElseThrow(() -> new BusinessException("El Área especificada no existe"));

        Equipo equipo = new Equipo();
        equipo.setCodigoInterno(equipoDTO.getCodigoInterno());
        equipo.setNumeroSerie(equipoDTO.getNumeroSerie());
        equipo.setNombre(equipoDTO.getNombre());
        equipo.setMarca(equipoDTO.getMarca());
        equipo.setModelo(equipoDTO.getModelo());
        equipo.setFechaCompra(equipoDTO.getFechaCompra());
        equipo.setVidaUtilAnios(equipoDTO.getVidaUtilAnios());
        equipo.setUbicacionActual(equipoDTO.getUbicacionActual());
        equipo.setTipoEquipo(tipo);
        equipo.setEstadoEquipo(estado);
        equipo.setArea(area);

        // Se guarda la entidad y se retorna convertida a DTO
        Equipo guardado = equipoRepository.save(equipo);
        return convertirAEntityADto(guardado);

    }

    public EquipoDTO actualizarEquipo(Long id, EquipoDTO equipoActualizadoDto, String username) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));

        // RN-01: validar duplicado en update
        if (equipoRepository.existsByNumeroSerieAndIdNot(equipoActualizadoDto.getNumeroSerie(), id)) {
            throw new BusinessException("El número de serie ya existe");
        }

        // Buscar las nuevas instancias de las relaciones
        TipoEquipo nuevoTipo = tipoEquipoRepository.findById(equipoActualizadoDto.getTipoEquipoId())
                .orElseThrow(() -> new BusinessException("El Tipo de Equipo especificado no existe"));

        EstadoEquipo nuevoEstado = estadoEquipoRepository.findByNombre(equipoActualizadoDto.getEstadoEquipoNombre())
                .orElseThrow(() -> new BusinessException("El Estado de Equipo especificado no existe"));
        
        Area nuevaArea = areaRepository.findById(equipoActualizadoDto.getAreaId())
            .orElseThrow(() -> new BusinessException("El Área especificada no existe"));

        // RN-03: transición de estado
        validarTransicionEstado(equipo.getEstadoEquipo(), nuevoEstado);

        // Temporal para validación de vida útil
        Equipo temporalValidacion = new Equipo();
        temporalValidacion.setFechaCompra(equipoActualizadoDto.getFechaCompra());
        temporalValidacion.setVidaUtilAnios(equipoActualizadoDto.getVidaUtilAnios());
        temporalValidacion.setEstadoEquipo(nuevoEstado);
        
        // RN-04: vida útil
        validarVidaUtil(temporalValidacion);

        // ACTUALIZACIÓN CON HISTORIAL
        if (!equipo.getNombre().equals(equipoActualizadoDto.getNombre())) {
            guardarHistorial(equipo, "nombre", equipo.getNombre(), equipoActualizadoDto.getNombre(), username);
        }
        if (!equipo.getEstadoEquipo().getId().equals(nuevoEstado.getId())) {
            guardarHistorial(equipo, "estadoEquipo", equipo.getEstadoEquipo().getNombre(), nuevoEstado.getNombre(), username);
        }

        // Actualizar campos de la entidad original
        equipo.setCodigoInterno(equipoActualizadoDto.getCodigoInterno());
        equipo.setNombre(equipoActualizadoDto.getNombre());
        equipo.setNumeroSerie(equipoActualizadoDto.getNumeroSerie());
        equipo.setMarca(equipoActualizadoDto.getMarca());
        equipo.setModelo(equipoActualizadoDto.getModelo());
        equipo.setTipoEquipo(nuevoTipo);
        equipo.setEstadoEquipo(nuevoEstado);
        equipo.setArea(nuevaArea);
        equipo.setFechaCompra(equipoActualizadoDto.getFechaCompra());
        equipo.setVidaUtilAnios(equipoActualizadoDto.getVidaUtilAnios());
        equipo.setUbicacionActual(equipoActualizadoDto.getUbicacionActual());

        Equipo guardado = equipoRepository.save(equipo);
        return convertirAEntityADto(guardado);
    }

    public void darDeBaja(Long id, String motivo, String autorizado) {
            Equipo e = equipoRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("El equipo no existe"));
    
            if (motivo == null || motivo.trim().isEmpty() || autorizado == null || autorizado.trim().isEmpty()) {
                throw new BusinessException("Motivo y autorización obligatorios");
            }

            e.setMotivoBaja(motivo);
            e.setAutorizadoPor(autorizado);
            e.setFechaBaja(LocalDate.now());

            EstadoEquipo estadoBaja = estadoEquipoRepository.findByNombre("DADO_DE_BAJA")
                    .orElseThrow(() -> new BusinessException("El estado DADO_DE_BAJA no está configurado en el sistema"));
            
            e.setEstadoEquipo(estadoBaja);
            equipoRepository.save(e);
        }

        public Map<String, Long> reportePorEstado() {
            Map<String, Long> map = new HashMap<>();
            for (Object[] obj : equipoRepository.countByEstado()) {
                map.put((String) obj[0], (Long) obj[1]);
            }
            return map;
        }

    // ==========================================
    // MÉTODOS PRIVADOS DE APOYO Y MAPEO
    // ==========================================

    private EquipoDTO convertirAEntityADto(Equipo equipo) {
        EquipoDTO dto = new EquipoDTO();
        dto.setId(equipo.getId());
        dto.setCodigoInterno(equipo.getCodigoInterno());
        dto.setNumeroSerie(equipo.getNumeroSerie());
        dto.setNombre(equipo.getNombre());
        dto.setMarca(equipo.getMarca());
        dto.setModelo(equipo.getModelo());
        dto.setFechaCompra(equipo.getFechaCompra());
        dto.setVidaUtilAnios(equipo.getVidaUtilAnios());
        dto.setUbicacionActual(equipo.getUbicacionActual());
        
        // Mapear IDs y Nombres de las relaciones para evitar LazyInitializationException
        if (equipo.getTipoEquipo() != null) {
            dto.setTipoEquipoId(equipo.getTipoEquipo().getId());
            dto.setTipoEquipoNombre(equipo.getTipoEquipo().getNombre());
        }
        if (equipo.getEstadoEquipo() != null) {
            dto.setTipoEquipoNombre(equipo.getTipoEquipo().getNombre());       
            dto.setEstadoEquipoNombre(equipo.getEstadoEquipo().getNombre());   
        }
        if (equipo.getArea() != null) {
            dto.setAreaId(equipo.getArea().getId());
            dto.setAreaNombre(equipo.getArea().getNombre());
        }

        dto.setMotivoBaja(equipo.getMotivoBaja());
        dto.setAutorizadoPor(equipo.getAutorizadoPor());
        dto.setFechaBaja(equipo.getFechaBaja());
        dto.setCreatedAt(equipo.getCreatedAt());
        dto.setUpdatedAt(equipo.getUpdatedAt());
        return dto;
    }

    private void guardarHistorial(Equipo equipo, String campo, String oldVal, String newVal, String user) {
        EquipoHistorial h = new EquipoHistorial();
        h.setEquipo(equipo);
        h.setCampoModificado(campo);
        h.setValorAnterior(oldVal);
        h.setValorNuevo(newVal);
        h.setFechaCambio(LocalDateTime.now());
        h.setUsuario(user != null ? user : "SISTEMA");

        historialRepository.save(h);
    }

    public List<EquipoHistorialDTO> obtenerHistorial(Long equipoId) {
        return historialRepository.findByEquipoId(equipoId).stream()
                .map(h -> new EquipoHistorialDTO(
                    h.getId(), h.getCampoModificado(), h.getValorAnterior(), 
                    h.getValorNuevo(), h.getFechaCambio(), h.getUsuario(),
                    h.getEquipo().getId(), h.getEquipo().getCodigoInterno(), h.getEquipo().getNombre()
                )).toList();
    }

    private void validarTransicionEstado(EstadoEquipo actual, EstadoEquipo nuevo) {
        // Verificamos que los objetos no sean nulos para evitar NullPointerException
        if (actual == null || nuevo == null) return;

        String nombreActual = actual.getNombre();
        String nombreNuevo = nuevo.getNombre();

        // Regla: No se puede pasar de DADO_BAJA a OPERATIVO
        if (nombreActual.equals("DADO_BAJA") && nombreNuevo.equals("OPERATIVO")) {
            throw new BusinessException("Regla de Negocio: No se puede pasar de baja a operativo directamente");
        }
        
        // Regla: No se puede modificar un equipo que ya está DADO_BAJA
        if (nombreActual.equals("DADO_DE_BAJA") || nombreActual.equals("DADO_BAJA")) {
            throw new BusinessException("Regla de Negocio: No se puede modificar un equipo que ya está dado de baja");
        }
    }

    private void validarVidaUtil(Equipo equipo) {
        if (equipo.getFechaCompra() == null || equipo.getVidaUtilAnios() == null) {
            return;
        }

        // Tu fórmula calcula el vencimiento sumando los años convertidos a meses
        LocalDate fechaVencimiento = equipo.getFechaCompra()
                .plusYears(equipo.getVidaUtilAnios());

        boolean vencido = LocalDate.now().isAfter(fechaVencimiento);

        if (vencido && equipo.getEstadoEquipo() != null && "OPERATIVO".equals(equipo.getEstadoEquipo().getNombre())) {
            throw new BusinessException("El equipo está vencido y no puede estar en estado OPERATIVO");
        }
    }
    public List<EquipoDTO> listarPorArea(Long areaId) {
        return equipoRepository.findByAreaId(areaId).stream()
                .map(this::convertirAEntityADto)
                .toList();
    }
}

