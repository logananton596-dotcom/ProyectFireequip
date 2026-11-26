package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.dto.UbicacionDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Area;
import com.fireequipmanager.backend.model.Ubicacion;
import com.fireequipmanager.backend.model.enumsUbicacion.TipoUbicacion;
import com.fireequipmanager.backend.repository.AreaRepository;
import com.fireequipmanager.backend.repository.UbicacionRepository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fireequipmanager.backend.repository.BomberoRepository;
import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final AreaRepository areaRepository;
    private final BomberoRepository bomberoRepository;

    public UbicacionService(
            UbicacionRepository ubicacionRepository,
            AreaRepository areaRepository,
            BomberoRepository bomberoRepository) {
        this.ubicacionRepository = ubicacionRepository;
        this.areaRepository = areaRepository;
        this.bomberoRepository = bomberoRepository;
    }

    // Lista todas
    public List<UbicacionDTO> listarTodas() {
        return ubicacionRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Lista activas
    public List<UbicacionDTO> listarActivas() {
        return ubicacionRepository.findByActivaTrue()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Lista por tipo
    public List<UbicacionDTO> listarPorTipo(TipoUbicacion tipo) {
        return ubicacionRepository.findByTipo(tipo)
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Lista hijas directas
    public List<UbicacionDTO> listarHijas(Long padreId) {
        return ubicacionRepository.findByPadreId(padreId)
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Lista raíces (sin padre)
    public List<UbicacionDTO> listarRaices() {
        return ubicacionRepository.findByPadreIsNull()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Busca por id
    public UbicacionDTO buscarPorId(@NonNull Long id) {
        return entityToDto(obtenerUbicacion(id));
    }

    // Crear
    public UbicacionDTO crear(@NonNull UbicacionDTO dto) {
        validarDatos(dto);
        validarCodigoDuplicado(dto, null);

        Ubicacion padre = null;
        if (dto.getPadreId() != null) {
            padre = obtenerUbicacion(requireNonNull(dto.getPadreId()));
            validarPadrePermitido(dto.getTipo(), padre);
        }

        Area area = null;
        if (dto.getAreaId() != null) {
            area = areaRepository.findById(requireNonNull(dto.getAreaId()))
                    .orElseThrow(() -> new BusinessException(
                            "Área no encontrada con id: " + dto.getAreaId()));
        }

        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setCodigo(dto.getCodigo().trim());
        ubicacion.setNombre(dto.getNombre().trim());
        ubicacion.setTipo(dto.getTipo());
        ubicacion.setTipoPersonalizado(dto.getTipoPersonalizado());
        ubicacion.setPadre(padre);
        ubicacion.setArea(area);
        ubicacion.setActiva(dto.getActiva() != null ? dto.getActiva() : true);
        ubicacion.setObservacion(dto.getObservacion());

        return entityToDto(ubicacionRepository.save(ubicacion));
    }

    // Actualizar
    public UbicacionDTO actualizar(@NonNull Long id, @NonNull UbicacionDTO dto) {
        Ubicacion ubicacion = obtenerUbicacion(id);
        validarDatos(dto);
        validarCodigoDuplicado(dto, id);

        // Validar padre: no puede ser ella misma ni un descendiente
        if (dto.getPadreId() != null) {
            if (dto.getPadreId().equals(id)) {
                throw new BusinessException("Una ubicación no puede ser su propio padre");
            }
            Ubicacion padre = obtenerUbicacion(requireNonNull(dto.getPadreId()));
            validarPadrePermitido(dto.getTipo(), padre);
            if (esDescendiente(id, dto.getPadreId())) {
                throw new BusinessException(
                        "No se puede asignar como padre a una ubicación descendiente");
            }
            ubicacion.setPadre(padre);
        } else {
            ubicacion.setPadre(null);
        }

        Area area = null;
        if (dto.getAreaId() != null) {
            area = areaRepository.findById(requireNonNull(dto.getAreaId()))
                    .orElseThrow(() -> new BusinessException(
                            "Área no encontrada con id: " + dto.getAreaId()));
        }
        ubicacion.setArea(area);

        ubicacion.setCodigo(dto.getCodigo().trim());
        ubicacion.setNombre(dto.getNombre().trim());
        ubicacion.setTipo(dto.getTipo());
        ubicacion.setTipoPersonalizado(dto.getTipoPersonalizado());
        ubicacion.setObservacion(dto.getObservacion());

        if (dto.getActiva() != null) {
            ubicacion.setActiva(dto.getActiva());
        }

        return entityToDto(ubicacionRepository.save(ubicacion));
    }

    // Activar / desactivar
    public UbicacionDTO cambiarActiva(@NonNull Long id, boolean activa) {
        Ubicacion ubicacion = obtenerUbicacion(id);
        ubicacion.setActiva(activa);
        return entityToDto(ubicacionRepository.save(ubicacion));
    }

    // Eliminar
    public void eliminar(@NonNull Long id) {
        Ubicacion ubicacion = obtenerUbicacion(id);
        validarEliminacion(ubicacion);
        ubicacionRepository.delete(requireNonNull(ubicacion));
    }

    // ==================== VALIDACIONES ====================

    private void validarDatos(UbicacionDTO dto) {
        if (dto == null) {
            throw new BusinessException("Los datos de la ubicación no pueden ser nulos");
        }
        if (dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            throw new BusinessException("El código es obligatorio");
        }
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BusinessException("El nombre es obligatorio");
        }
        if (dto.getTipo() == null) {
            throw new BusinessException("Debe seleccionar el tipo de ubicación");
        }
        if (dto.getTipo() == TipoUbicacion.OTRO
                && (dto.getTipoPersonalizado() == null || dto.getTipoPersonalizado().isBlank())) {
            throw new BusinessException(
                    "Debe especificar el tipo personalizado cuando selecciona OTRO");
        }
    }

    private void validarCodigoDuplicado(UbicacionDTO dto, Long id) {
        if (id == null) {
            if (ubicacionRepository.existsByCodigo(dto.getCodigo())) {
                throw new BusinessException("El código de ubicación ya existe");
            }
            return;
        }
        if (ubicacionRepository.existsByCodigoAndIdNot(dto.getCodigo(), id)) {
            throw new BusinessException("El código de ubicación pertenece a otra ubicación");
        }
    }

    // Reglas de jerarquía por tipo
    private void validarPadrePermitido(TipoUbicacion tipoHijo, Ubicacion padre) {
        TipoUbicacion tipoPadre = padre.getTipo();

        switch (tipoHijo) {
            case PISO -> throw new BusinessException(
                    "Un piso no puede tener padre");
            case OFICINA -> {
                if (tipoPadre != TipoUbicacion.PISO) {
                    throw new BusinessException(
                            "Una oficina debe colgar de un PISO");
                }
            }
            case CASILLERO -> {
                if (tipoPadre != TipoUbicacion.OFICINA
                        && tipoPadre != TipoUbicacion.PISO
                        && tipoPadre != TipoUbicacion.PASILLO) {
                    throw new BusinessException(
                            "Un casillero debe colgar de una OFICINA, PISO o PASILLO");
                }
            }
            case COMPARTIMIENTO -> {
                if (tipoPadre != TipoUbicacion.VEHICULO) {
                    throw new BusinessException(
                            "Un compartimiento debe colgar de un VEHICULO");
                }
            }
            case VEHICULO -> {
                if (tipoPadre != TipoUbicacion.GARAJE
                        && tipoPadre != TipoUbicacion.PATIO
                        && tipoPadre != TipoUbicacion.PISO) {
                    throw new BusinessException(
                            "Un vehículo debe colgar de un GARAJE, PATIO o PISO");
                }
            }
            // PASILLO, PATIO, GARAJE, DEPOSITO, OTRO: sin restricción estricta
            default -> { }
        }
    }

    // Verifica que candidatoId sea descendiente de raizId
    private boolean esDescendiente(Long raizId, Long candidatoId) {
        Ubicacion candidato = ubicacionRepository.findById(requireNonNull(candidatoId)).orElse(null);
        while (candidato != null) {
            if (candidato.getPadre() != null
                    && Objects.equals(candidato.getPadre().getId(), raizId)) {
                return true;
            }
            candidato = candidato.getPadre();
        }
        return false;
    }

    private void validarEliminacion(Ubicacion ubicacion) {
        if (ubicacionRepository.existsByPadreId(ubicacion.getId())) {
            throw new BusinessException(
                    "No se puede eliminar una ubicación que tiene sub-ubicaciones");
        }
        if (ubicacionRepository.existsEquipoEnUbicacion(ubicacion.getId())) {
            throw new BusinessException(
                    "No se puede eliminar: hay equipos en esta ubicación");
        }
        if (ubicacionRepository.existsEppEnUbicacion(ubicacion.getId())) {
            throw new BusinessException(
                    "No se puede eliminar: hay EPP en esta ubicación");
        }
        if (ubicacionRepository.existsBienEnUbicacion(ubicacion.getId())) {
            throw new BusinessException(
                    "No se puede eliminar: hay bienes en esta ubicación");
        }
    }

    // ==================== HELPERS ====================

    private Ubicacion obtenerUbicacion(@NonNull Long id) {
        return ubicacionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Ubicación no encontrada con id: " + id));
    }

    private UbicacionDTO entityToDto(Ubicacion ubicacion) {
        UbicacionDTO dto = new UbicacionDTO();

        dto.setId(ubicacion.getId());
        dto.setCodigo(ubicacion.getCodigo());
        dto.setNombre(ubicacion.getNombre());
        dto.setTipo(ubicacion.getTipo());
        dto.setTipoPersonalizado(ubicacion.getTipoPersonalizado());

        if (ubicacion.getPadre() != null) {
            dto.setPadreId(ubicacion.getPadre().getId());
            dto.setPadreNombre(ubicacion.getPadre().getNombre());
        }

        if (ubicacion.getArea() != null) {
            dto.setAreaId(ubicacion.getArea().getId());
            dto.setAreaNombre(ubicacion.getArea().getNombreArea());
        }

        dto.setActiva(ubicacion.getActiva());
        dto.setObservacion(ubicacion.getObservacion());
        if (ubicacion.getTipo() == TipoUbicacion.CASILLERO) {
            bomberoRepository.findByUbicacionId(ubicacion.getId())
                    .ifPresent(b -> {
                        dto.setBomberoAsignadoId(b.getId());
                        dto.setBomberoAsignadoNombre(
                                b.getApellido() + ", " + b.getNombre()
                        );
                    });
        }
        dto.setCreatedAt(ubicacion.getCreatedAt());
        dto.setUpdatedAt(ubicacion.getUpdatedAt());

        return dto;
    }
}