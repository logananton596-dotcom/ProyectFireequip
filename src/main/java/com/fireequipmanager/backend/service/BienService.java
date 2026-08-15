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

@Service
@Transactional
public class BienService {

    private final BienRepository bienRepository;
    private final UbicacionRepository ubicacionRepository;

    public BienService(
            BienRepository bienRepository,
            UbicacionRepository ubicacionRepository) {

        this.bienRepository = bienRepository;
        this.ubicacionRepository = ubicacionRepository;
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

        Ubicacion ubicacion =
                obtenerUbicacionActiva(dto.getUbicacionId());

        Bien bien = dtoToEntity(dto);

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

        Ubicacion ubicacion =
                obtenerUbicacionActiva(dto.getUbicacionId());

        actualizarDatos(bien, dto);

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

        if (dto.getArea() == null) {

            throw new BusinessException(
                    "Debe seleccionar el área propietaria"
            );
        }

        if (dto.getUbicacionId() == null) {

            throw new BusinessException(
                    "Debe seleccionar la ubicación del bien"
            );
        }
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
        bien.setArea(dto.getArea());

        if (dto.getActivo() != null) {
            bien.setActivo(dto.getActivo());
        }
    }

    // Convierte entidad a DTO
    private BienDTO entityToDto(Bien bien) {

        if (bien == null) {
            return null;
        }

        Ubicacion ubicacion = bien.getUbicacion();

        return new BienDTO(
                bien.getId(),
                bien.getCodigoCgbvp(),
                bien.getNombre(),
                bien.getTipoBien(),
                bien.getMarca(),
                bien.getModelo(),
                bien.getNumeroSerie(),
                bien.getFechaAdquisicion(),
                bien.getEstado(),
                bien.getCondicion(),
                bien.getValorReferencial(),
                bien.getEspecificaciones(),
                bien.getObservaciones(),
                bien.getArea(),
                ubicacion != null
                        ? ubicacion.getId()
                        : null,
                ubicacion != null
                        ? ubicacion.getCodigo()
                        : null,
                ubicacion != null
                        ? ubicacion.getNombreUbicacion()
                        : null,
                bien.getActivo()
        );
    }
}