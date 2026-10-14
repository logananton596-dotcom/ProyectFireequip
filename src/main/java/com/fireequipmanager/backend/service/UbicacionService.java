package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.dto.UbicacionDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Ubicacion;
import com.fireequipmanager.backend.model.enumsUbicacion.EstadoUbicacion;
import com.fireequipmanager.backend.model.enumsUbicacion.NombreUbicacion;
import com.fireequipmanager.backend.repository.UbicacionRepository;

import jakarta.transaction.Transactional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;

    public UbicacionService(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    // Lista todas las ubicaciones
    public List<UbicacionDTO> listarTodos() {
        return ubicacionRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Lista únicamente las ubicaciones activas
    public List<UbicacionDTO> listarActivas() {
        return ubicacionRepository.findByActivaTrue()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Busca una ubicación por ID
    public UbicacionDTO buscarPorId(@NonNull Long id) {
        return entityToDto(obtenerUbicacion(id));
    }
        // Busca una ubicación por nombre
    public UbicacionDTO buscarPorNombre(@NonNull NombreUbicacion nombreUbicacion) {
        return ubicacionRepository
                .findByNombreUbicacion(nombreUbicacion) // Ahora es un Optional
                .map(this::entityToDto)
                .orElseThrow(() -> new BusinessException("Ubicación no encontrada"));
    }

    // Lista ubicaciones por tipo
    public List<UbicacionDTO> listarPorTipo(
            @NonNull NombreUbicacion nombreUbicacion) {

        return ubicacionRepository
                .findByNombreUbicacionAndActivaTrue(nombreUbicacion)
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Registra una nueva ubicación
    public UbicacionDTO crear(UbicacionDTO dto) {

        validarDatos(dto);
        validarDuplicados(dto, null);

        Ubicacion ubicacion = dtoToEntity(dto);

        if (ubicacion.getEstado() == null) {
            ubicacion.setEstado(
                    EstadoUbicacion.ACTIVA
            );
        }

        if (ubicacion.getActiva() == null) {
            ubicacion.setActiva(true);
        }

        return entityToDto(
                ubicacionRepository.save(ubicacion)
        );
    }

    // Actualiza una ubicación
    public UbicacionDTO actualizar(
            @NonNull Long id,
            UbicacionDTO dto) {

        Ubicacion ubicacion =
                obtenerUbicacion(id);

        validarDatos(dto);
        validarDuplicados(dto, id);

        actualizarDatos(
                ubicacion,
                dto
        );

        return entityToDto(
                ubicacionRepository.save(Objects.requireNonNull(ubicacion, "Error al guardar la ubicación en el sistema"))
        );
    }

    // Activa una ubicación
    public UbicacionDTO activar(@NonNull Long id) {

        Ubicacion ubicacion = obtenerUbicacion(id);

        ubicacion.setActiva(true);
        ubicacion.setEstado(EstadoUbicacion.ACTIVA);

        return entityToDto(
                ubicacionRepository.save(ubicacion)
        );
    }

    // Desactiva una ubicación
    public UbicacionDTO desactivar(@NonNull Long id) {

        Ubicacion ubicacion = obtenerUbicacion(id);

        ubicacion.setActiva(false);
        ubicacion.setEstado(EstadoUbicacion.INACTIVA);

        return entityToDto(
                ubicacionRepository.save(ubicacion)
        );
    }

    // Elimina una ubicación
    public void eliminar(@NonNull Long id) {

        Ubicacion ubicacion = obtenerUbicacion(id);

        validarEliminacion(ubicacion);

        ubicacionRepository.delete(Objects.requireNonNull(ubicacion, "La ubicación a eliminar no puede ser nula"));
    }

    // Validaciones principales
    private void validarDatos(UbicacionDTO dto) {

        if (dto == null) {
            throw new BusinessException(
                    "Los datos de la ubicación no pueden ser nulos"
            );
        }

        if (dto.getCodigo() == null ||
                dto.getCodigo().isBlank()) {

            throw new BusinessException(
                    "El código de ubicación es obligatorio"
            );
        }

        if (dto.getNombreUbicacion() == null) {
            throw new BusinessException(
                    "Debe seleccionar un tipo de ubicación"
            );
        }
        if (dto.getEstado() == null) {

            throw new BusinessException(
                    "Debe seleccionar el estado de la ubicación"
            );
        }
    }

        // Valida códigos y nombres duplicados
    private void validarDuplicados(
            UbicacionDTO dto,
            Long id) {

        if (id == null) {
            if (ubicacionRepository
                    .existsByCodigo(dto.getCodigo())) {
                throw new BusinessException(
                        "El código de ubicación ya existe"
                );
            }

            if (ubicacionRepository
                    .existsByNombreUbicacion(
                            dto.getNombreUbicacion())) {
                throw new BusinessException(
                        "La ubicación seleccionada ya existe"
                );
            }
            return;
        }

        if (ubicacionRepository
                .existsByCodigoAndIdNot(
                        dto.getCodigo(),
                        id)) {
            throw new BusinessException(
                    "El código pertenece a otra ubicación"
            );
        }

        if (ubicacionRepository
                .existsByNombreUbicacionAndIdNot(
                        dto.getNombreUbicacion(),
                        id)) {
            throw new BusinessException(
                    "La ubicación pertenece a otro registro"
            );
        }
    }





    // Valida si una ubicación puede eliminarse
    private void validarEliminacion(Ubicacion ubicacion) {

        if (Boolean.TRUE.equals(ubicacion.getActiva())) {
            throw new BusinessException(
                    "No se puede eliminar una ubicación activa"
            );
        }

        // Aquí agregaremos posteriormente validaciones de referencias
        // de equipos, EPP, bienes, bomberos y vehículos.
    }

    // Obtiene una ubicación por ID
    private Ubicacion obtenerUbicacion(@NonNull Long id) {

        return ubicacionRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                "Ubicación no encontrada"
                        ));
    }

    // Convierte DTO a entidad
    private Ubicacion dtoToEntity(UbicacionDTO dto) {

        Ubicacion ubicacion = new Ubicacion();

        actualizarDatos(ubicacion, dto);

        return ubicacion;
    }

// Actualiza los datos de la ubicación
private void actualizarDatos(Ubicacion ubicacion, UbicacionDTO dto) {
    if (ubicacion == null || dto == null) {
        return;
    }

    // 🚀 1. CORREGIDO: Mapeamos el Enum al atributo correcto de tu entidad (nombreUbicacion)
    ubicacion.setNombreUbicacion(dto.getNombreUbicacion());

    // 🚀 2. CORREGIDO: Asignamos el código único (obligatorio en tu entidad)
    ubicacion.setCodigo(dto.getCodigo());

    // 🚀 3. OPTIMIZADO: Control seguro de nulidad para la bandera 'activa'
    // Si llega null desde el DTO, por defecto se mantiene en true
    ubicacion.setActiva(dto.getActiva() == null ? true : dto.getActiva());

    // 🚀 4. CONTROL AUTOMÁTICO DE ENUMS: Sincroniza el Enum de estado según la bandera booleana
    if (Boolean.TRUE.equals(ubicacion.getActiva())) {
        ubicacion.setEstado(EstadoUbicacion.ACTIVA);
    } else {
        // Asegúrate de que en tu Enum real esté escrito como INACTIVA o si es otra palabra del catálogo
        ubicacion.setEstado(EstadoUbicacion.INACTIVA); 
    }

    // 5. Asignamos las observaciones
    ubicacion.setObservacion(dto.getObservacion());
}


    // Convierte entidad a DTO
    private UbicacionDTO entityToDto(Ubicacion ubicacion) {
        if (ubicacion == null) {
            return null;
        }

        // Retornamos el DTO rellenando los campos de relaciones como null por defecto.
        // La carga real de ID y Nombres de Oficina/Casillero/Vehículo la harás mediante subconsultas 
        // en tu método del Service si es que usas relaciones cruzadas.
        return new UbicacionDTO(
                ubicacion.getId(),
                ubicacion.getCodigo(),
                ubicacion.getNombreUbicacion(),
                ubicacion.getEstado(),
                ubicacion.getObservacion(),
                null, // oficinaId
                null, // oficinaNombre
                null, // casilleroId
                null, // numeroCasillero
                null, // vehiculoId
                null, // vehiculoNombre
                null, // compartimientoId
                null, // compartimientoNombre
                ubicacion.getActiva()
        );
    }
}