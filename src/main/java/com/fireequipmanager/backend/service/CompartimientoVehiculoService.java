package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.dto.CompartimientoVehiculoDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.CompartimientoVehiculo;
import com.fireequipmanager.backend.model.Vehiculo;
import com.fireequipmanager.backend.repository.CompartimientoVehiculoRepository;
import com.fireequipmanager.backend.repository.VehiculoRepository;

import jakarta.transaction.Transactional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CompartimientoVehiculoService {

    private final CompartimientoVehiculoRepository compartimientoRepository;
    private final VehiculoRepository vehiculoRepository;

    public CompartimientoVehiculoService(
            CompartimientoVehiculoRepository compartimientoRepository,
            VehiculoRepository vehiculoRepository) {

        this.compartimientoRepository = compartimientoRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    // Lista todos los compartimientos
    public List<CompartimientoVehiculoDTO> listarTodos() {

        return compartimientoRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }
    // 🚀 NUEVO MÉTODO: Lista únicamente los compartimientos de vehículos activos
    public List<CompartimientoVehiculoDTO> listarActivos() {
        return compartimientoRepository.findByActivoTrue()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Lista los compartimientos de un vehículo
    public List<CompartimientoVehiculoDTO> listarPorVehiculo(
            @NonNull Long vehiculoId) {

        obtenerVehiculo(vehiculoId);

        return compartimientoRepository
                .findByVehiculoId(vehiculoId)
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Busca un compartimiento por ID
    public CompartimientoVehiculoDTO buscarPorId(
            @NonNull Long id) {

        return entityToDto(obtenerCompartimiento(id));
    }

    // Registra un nuevo compartimiento
    public CompartimientoVehiculoDTO crear(
            @NonNull CompartimientoVehiculoDTO dto) {

        validarDatos(dto);
        Vehiculo vehiculo = obtenerVehiculo(Objects.requireNonNull(dto.getVehiculoId(), "El ID del vehículo es obligatorio"));
        validarDuplicado(dto, null);
        CompartimientoVehiculo compartimiento = dtoToEntity(dto);
        compartimiento.setVehiculo(vehiculo);
        compartimiento.setActivo(true);
        return entityToDto(
                compartimientoRepository.save(compartimiento)
        );
    }

    // Actualiza un compartimiento
    public CompartimientoVehiculoDTO actualizar(
            @NonNull Long id,
            @NonNull CompartimientoVehiculoDTO dto) {

        CompartimientoVehiculo compartimiento =
                obtenerCompartimiento(id);

        validarDatos(dto);

        Vehiculo vehiculo = obtenerVehiculo(Objects.requireNonNull(dto.getVehiculoId(), "El ID del vehículo es obligatorio"));

        validarDuplicado(dto, id);

        actualizarDatos(compartimiento, dto);

        compartimiento.setVehiculo(vehiculo);

        return entityToDto(
                compartimientoRepository.save(compartimiento)
        );
    }

    // Activa un compartimiento
    public CompartimientoVehiculoDTO activar(
            @NonNull Long id) {

        CompartimientoVehiculo compartimiento =
                obtenerCompartimiento(id);

        compartimiento.setActivo(true);

        return entityToDto(
                compartimientoRepository.save(compartimiento)
        );
    }

    // Desactiva un compartimiento
    public CompartimientoVehiculoDTO desactivar(
            @NonNull Long id) {

        CompartimientoVehiculo compartimiento =
                obtenerCompartimiento(id);

        validarDesactivacion(compartimiento);

        compartimiento.setActivo(false);

        return entityToDto(
                compartimientoRepository.save(compartimiento)
        );
    }

    // Elimina un compartimiento
    public void eliminar(@NonNull Long id) {

        CompartimientoVehiculo compartimiento =
                obtenerCompartimiento(id);

        validarEliminacion(compartimiento);

        compartimientoRepository.delete(Objects.requireNonNull(compartimiento, "El compartimiento a eliminar no puede ser nulo"));
    }

    // Valida los datos básicos
    private void validarDatos(
            CompartimientoVehiculoDTO dto) {

        if (dto == null) {
            throw new BusinessException(
                    "Los datos del compartimiento no pueden ser nulos"
            );
        }

        if (dto.getVehiculoId() == null) {
            throw new BusinessException(
                    "Debe seleccionar un vehículo"
            );
        }

        if (dto.getTipoCompartimiento() == null) {
        throw new BusinessException(
                "El tipo de compartimiento es obligatorio"
        );
        }
    }

    // Valida duplicados dentro del mismo vehículo
    private void validarDuplicado(
            CompartimientoVehiculoDTO dto,
            Long id) {

        if (id == null) {

            if (compartimientoRepository
                    .existsByVehiculoIdAndTipoCompartimiento(
                            dto.getVehiculoId(),
                            dto.getTipoCompartimiento())) {

                throw new BusinessException(
                        "Ya existe ese compartimiento en el vehículo"
                );
            }

            return;
        }

        if (compartimientoRepository
                .existsByVehiculoIdAndTipoCompartimientoAndIdNot(
                        dto.getVehiculoId(),
                        dto.getTipoCompartimiento(),
                        id)) {

            throw new BusinessException(
                    "Ya existe otro compartimiento con ese nombre en el vehículo"
            );
        }
    }

    // Valida si puede desactivarse
    private void validarDesactivacion(
            CompartimientoVehiculo compartimiento) {

        // Aquí posteriormente validaremos equipos asignados.
    }

    // Valida si puede eliminarse
    private void validarEliminacion(
            CompartimientoVehiculo compartimiento) {

        if (Boolean.TRUE.equals(compartimiento.getActivo())) {

            throw new BusinessException(
                    "No se puede eliminar un compartimiento activo"
            );
        }

        // Aquí posteriormente validaremos equipos y EPP asignados.
    }

    // Obtiene un vehículo
    private Vehiculo obtenerVehiculo(@NonNull Long id) {

        return vehiculoRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                "Vehículo no encontrado"
                        ));
    }

    // Obtiene un compartimiento
    private CompartimientoVehiculo obtenerCompartimiento(
            @NonNull Long id) {

        return compartimientoRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                "Compartimiento no encontrado"
                        ));
    }

    // Convierte DTO a entidad
    private CompartimientoVehiculo dtoToEntity(
            CompartimientoVehiculoDTO dto) {

        CompartimientoVehiculo compartimiento =
                new CompartimientoVehiculo();

        actualizarDatos(compartimiento, dto);

        return compartimiento;
    }

    // Actualiza los datos
    private void actualizarDatos(
            CompartimientoVehiculo compartimiento,
            CompartimientoVehiculoDTO dto) {

        compartimiento.setTipoCompartimiento(
                Objects.requireNonNull(dto.getTipoCompartimiento(), "El tipo de compartimiento es obligatorio")
        );

        compartimiento.setDescripcion(
                Objects.requireNonNull(dto.getDescripcion(), "La descripción del compartimiento es obligatoria")
        );

        if (dto.getActivo() != null) {
            compartimiento.setActivo(dto.getActivo());
        }
    }

    // Convierte entidad a DTO
// Convierte entidad a DTO
private CompartimientoVehiculoDTO entityToDto(CompartimientoVehiculo compartimiento) {
    if (compartimiento == null) {
        return null;
    }
    return new CompartimientoVehiculoDTO(
            compartimiento.getId(),
            // 1. Validamos de forma segura la relación con Vehículo para evitar NullPointerException
            compartimiento.getVehiculo() != null ? compartimiento.getVehiculo().getId() : null,
            // 2. Cargamos el nombre o código del vehículo para la respuesta visual del Frontend
            compartimiento.getVehiculo() != null ? compartimiento.getVehiculo().getCodigo() : null, // Ajusta a .getNombre() si usas esa propiedad
            // 3. Pasamos el Enum de Tipo de Compartimiento en su posición exacta
            compartimiento.getTipoCompartimiento(),
            // 4. Mapeamos el resto de propiedades del DTO en el orden del constructor
            compartimiento.getCodigo(),
            compartimiento.getDescripcion(),
            compartimiento.getOrden(),
            compartimiento.getActivo()
    );
}

}