package com.fireequipmanager.backend.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fireequipmanager.backend.dto.BienDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Bien;
import com.fireequipmanager.backend.model.Ubicacion;
import com.fireequipmanager.backend.repository.BienRepository;
import com.fireequipmanager.backend.repository.UbicacionRepository;
import com.fireequipmanager.backend.model.Area;
import com.fireequipmanager.backend.repository.AreaRepository;

@Service
@Transactional
public class BienService {

    private final BienRepository bienRepository;
    private final UbicacionRepository ubicacionRepository;
    private final AreaRepository areaRepository;
    public BienService(
            BienRepository bienRepository,
            UbicacionRepository ubicacionRepository,
            AreaRepository areaRepository) {

        this.bienRepository = bienRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.areaRepository = areaRepository;
    }

    // Lista todos los bienes
    public List<BienDTO> listarTodos() {

        return bienRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Lista únicamente los bienes activos
    public List<BienDTO> listarActivos() {

        return bienRepository.findByActivoTrue()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Busca un bien por ID
    public BienDTO buscarPorId(Long id) {

        return entityToDto(
                obtenerBien(id)
        );
    }

    // Registra un nuevo bien
    public BienDTO crear(BienDTO dto) {

        validarDatos(dto);
        validarCodigoDuplicado(dto, null);

        Area area = obtenerArea(dto.getAreaId());
        Ubicacion ubicacion = obtenerUbicacionActiva(dto.getUbicacionId());

        Bien bien = dtoToEntity(dto);

        bien.setArea(area);
        bien.setUbicacion(ubicacion);

        if (bien.getActivo() == null) {
            bien.setActivo(true);
        }

        return entityToDto(
                bienRepository.save(bien)
        );
    }

    // Actualiza un bien
    public BienDTO actualizar(Long id, BienDTO dto) {

        Bien bien = obtenerBien(id);

        validarDatos(dto);
        validarCodigoDuplicado(dto, id);

        Area area = obtenerArea(dto.getAreaId());
        Ubicacion ubicacion = obtenerUbicacionActiva(dto.getUbicacionId());

        actualizarDatos(bien, dto);

        bien.setArea(area);
        bien.setUbicacion(ubicacion);

        return entityToDto(
                bienRepository.save(bien)
        );
    }

    // Desactiva un bien
    public BienDTO desactivar(Long id) {

        Bien bien = obtenerBien(id);

        bien.setActivo(false);

        return entityToDto(
                bienRepository.save(bien)
        );
    }

    // Activa un bien
    public BienDTO activar(Long id) {

        Bien bien = obtenerBien(id);

        bien.setActivo(true);

        return entityToDto(
                bienRepository.save(bien)
        );
    }

    // Elimina un bien
    public void eliminar(Long id) {

        Bien bien = obtenerBien(id);

        if (Boolean.TRUE.equals(bien.getActivo())) {
            throw new BusinessException(
                    "No se puede eliminar un bien activo"
            );
        }

        bienRepository.delete(bien);
    }

    // Valida los datos principales
    private void validarDatos(BienDTO dto) {

        if (dto == null) {
            throw new BusinessException(
                    "Los datos del bien no pueden ser nulos"
            );
        }

        if (dto.getCodigoCgbvp() == null ||
                dto.getCodigoCgbvp().isBlank()) {

            throw new BusinessException(
                    "El código de inventario es obligatorio"
            );
        }

        if (dto.getNombre() == null ||
                dto.getNombre().isBlank()) {

            throw new BusinessException(
                    "El nombre del bien es obligatorio"
            );
        }

        if (dto.getTipoBien() == null) {

            throw new BusinessException(
                    "Debe seleccionar el tipo de bien"
            );
        }

        if (dto.getFechaAdquisicion() == null) {

            throw new BusinessException(
                    "La fecha de adquisición es obligatoria"
            );
        }

        if (dto.getEstado() == null) {

            throw new BusinessException(
                    "Debe seleccionar el estado del bien"
            );
        }

        if (dto.getAreaId() == null) {
        throw new BusinessException("Debe seleccionar el área propietaria");
        }

        if (dto.getUbicacionId() == null) {

            throw new BusinessException(
                    "Debe seleccionar la ubicación del bien"
            );
        }
    }

    private Area obtenerArea(Long areaId) {
    if (areaId == null) {
        throw new BusinessException("El área es obligatoria");
    }
    return areaRepository.findById(areaId)
            .orElseThrow(() -> new BusinessException("Área no encontrada con id: " + areaId));
        }

    // Valida que el código no esté duplicado
    private void validarCodigoDuplicado(
            BienDTO dto,
            Long id) {

        if (id == null) {

            if (bienRepository.existsByCodigoCgbvp(
                    dto.getCodigoCgbvp())) {

                throw new BusinessException(
                        "El código de inventario ya existe"
                );
            }

            return;
        }

        if (bienRepository.existsByCodigoCgbvpAndIdNot(
                dto.getCodigoCgbvp(),
                id)) {

            throw new BusinessException(
                    "El código de inventario pertenece a otro bien"
            );
        }
    }

    // Obtiene un bien por ID
    private Bien obtenerBien(Long id) {

        return bienRepository.findById(Objects.requireNonNull(id, "El ID del bien es obligatorio"))
                .orElseThrow(() ->
                        new BusinessException(
                                "Bien no encontrado"
                        ));
    }

    // Obtiene una ubicación activa
    private Ubicacion obtenerUbicacionActiva(Long ubicacionId) {

        Ubicacion ubicacion =
                ubicacionRepository.findById(Objects.requireNonNull(ubicacionId))
                        .orElseThrow(() ->
                                new BusinessException(
                                        "La ubicación no existe"
                                ));

        if (!Boolean.TRUE.equals(ubicacion.getActiva())) {

            throw new BusinessException(
                    "La ubicación seleccionada está inactiva"
            );
        }

        return ubicacion;
    }

    // Convierte DTO a entidad
    private Bien dtoToEntity(BienDTO dto) {

        Bien bien = new Bien();

        actualizarDatos(bien, dto);

        return bien;
    }

    // Actualiza los datos del bien
    private void actualizarDatos(
            Bien bien,
            BienDTO dto) {

        bien.setCodigoCgbvp(dto.getCodigoCgbvp());
        bien.setNombre(dto.getNombre());
        bien.setTipoBien(dto.getTipoBien());
        bien.setMarca(dto.getMarca());
        bien.setModelo(dto.getModelo());
        bien.setNumeroSerie(dto.getNumeroSerie());
        bien.setFechaAdquisicion(dto.getFechaAdquisicion());
        bien.setEstado(dto.getEstado());
        bien.setCondicion(dto.getCondicion());
        bien.setValorReferencial(dto.getValorReferencial());
        bien.setEspecificaciones(dto.getEspecificaciones());
        bien.setObservaciones(dto.getObservaciones());
        

        if (dto.getActivo() != null) {
            bien.setActivo(dto.getActivo());
        }
    }

    // Convierte entidad a DTO
 private BienDTO entityToDto(Bien bien) {
    if (bien == null) {
        return null;
    }

    BienDTO dto = new BienDTO();
    dto.setId(bien.getId());
    dto.setCodigoCgbvp(bien.getCodigoCgbvp());
    dto.setNombre(bien.getNombre());
    dto.setTipoBien(bien.getTipoBien());
    dto.setMarca(bien.getMarca());
    dto.setModelo(bien.getModelo());
    dto.setNumeroSerie(bien.getNumeroSerie());
    dto.setFechaAdquisicion(bien.getFechaAdquisicion());
    dto.setEstado(bien.getEstado());
    dto.setCondicion(bien.getCondicion());
    dto.setValorReferencial(bien.getValorReferencial());
    dto.setEspecificaciones(bien.getEspecificaciones());
    dto.setObservaciones(bien.getObservaciones());
    dto.setActivo(bien.getActivo());

    // Relación de Área propietaria
    if (bien.getArea() != null) {
        dto.setAreaId(bien.getArea().getId());
        dto.setAreaNombre(bien.getArea().getNombreArea());
    }

    // Relación de Ubicación jerárquica
    if (bien.getUbicacion() != null) {
        dto.setUbicacionId(bien.getUbicacion().getId());
        dto.setUbicacionCodigo(bien.getUbicacion().getCodigo());
        dto.setUbicacionNombre(bien.getUbicacion().getNombre());
        dto.setUbicacionTipo(
            bien.getUbicacion().getTipo() != null ? bien.getUbicacion().getTipo().name() : null
        );
        dto.setUbicacionDisplay(
            bien.getUbicacion().getCodigo() + " - " + bien.getUbicacion().getNombre()
        );
    }

    return dto;
}
}
