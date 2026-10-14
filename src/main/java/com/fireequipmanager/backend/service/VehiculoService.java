package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.dto.VehiculoDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Vehiculo;
import com.fireequipmanager.backend.model.enumsVehiculo.EstadoVehiculo;
import com.fireequipmanager.backend.repository.VehiculoRepository;

import jakarta.transaction.Transactional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    // Lista todos los vehículos
    public List<VehiculoDTO> listarTodos() {

        return vehiculoRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Lista vehículos operativos
    public List<VehiculoDTO> listarOperativos() {

        return vehiculoRepository
                .findByEstado(
                        com.fireequipmanager.backend.model.enumsVehiculo.EstadoVehiculo.OPERATIVO
                )
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Lista todos los vehículos que no están dados de baja lógica (activo = true)
    public List<VehiculoDTO> listarActivos() {
        return vehiculoRepository.findByActivoTrue()
                .stream()
                .map(this::entityToDto)
                .toList();
    }
    public List<VehiculoDTO> listarActivosOperativos() {
        return vehiculoRepository.findByEstadoAndActivoTrue(EstadoVehiculo.OPERATIVO)
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Busca un vehículo por ID
    public VehiculoDTO buscarPorId(@NonNull Long id) {

        return entityToDto(obtenerVehiculo(id));
    }

    // Registra un vehículo
    public VehiculoDTO crear(@NonNull VehiculoDTO dto) {

        validarDatos(dto);
        validarPlaca(dto, null);

        Vehiculo vehiculo = dtoToEntity(dto);

        return entityToDto(
                vehiculoRepository.save(Objects.requireNonNull(vehiculo, "El vehículo a guardar no puede ser nulo"))
        );
    }

    // Actualiza un vehículo
    public VehiculoDTO actualizar(
            @NonNull Long id,
            @NonNull VehiculoDTO dto) {

        Vehiculo vehiculo = obtenerVehiculo(id);

        validarDatos(dto);
        validarPlaca(dto, id);

        actualizarDatos(vehiculo, dto);

        return entityToDto(
                vehiculoRepository.save(Objects.requireNonNull(vehiculo, "El vehículo a guardar no puede ser nulo"))
        );
    }

    // Elimina un vehículo
    public void eliminar(@NonNull Long id) {

        Vehiculo vehiculo = obtenerVehiculo(id);

        validarEliminacion(vehiculo);

        vehiculoRepository.delete(Objects.requireNonNull(vehiculo, "El vehículo a eliminar no puede ser nulo"));
    }
    // Activa un vehículo (Alta lógica)
    public VehiculoDTO activar(@NonNull Long id) {
        // 1. Recuperamos el vehículo de la base de datos de forma segura
        Vehiculo vehiculo = obtenerVehiculo(id);

        // 2. Modificamos su estado administrativo y su disponibilidad mecánica
        vehiculo.setActivo(true);
        vehiculo.setEstado(com.fireequipmanager.backend.model.enumsVehiculo.EstadoVehiculo.OPERATIVO);

        // 3. Persistimos los cambios y retornamos el DTO transformado
        return entityToDto(vehiculoRepository.save(vehiculo));
    }

    // Desactiva un vehículo (Baja lógica)
    public VehiculoDTO desactivar(@NonNull Long id) {
        // 1. Recuperamos el vehículo
        Vehiculo vehiculo = obtenerVehiculo(id);

        // 2. Modificamos su estado administrativo y consistente con el Enum de mantenimiento
        vehiculo.setActivo(false);
        vehiculo.setEstado(com.fireequipmanager.backend.model.enumsVehiculo.EstadoVehiculo.BAJA);

        // 3. Persistimos los cambios y retornamos el DTO
        return entityToDto(vehiculoRepository.save(vehiculo));
    }


    // Valida los datos básicos
    private void validarDatos(VehiculoDTO dto) {

        if (dto == null) {
            throw new BusinessException(
                    "Los datos del vehículo no pueden ser nulos"
            );
        }

        if (dto.getNombreVehiculo() == null) {
            throw new BusinessException(
                    "El nombre del vehículo es obligatorio"
            );
        }

        if (dto.getPlaca() == null ||
                dto.getPlaca().isBlank()) {

            throw new BusinessException(
                    "La placa del vehículo es obligatoria"
            );
        }

        if (dto.getMarca() == null ) {
            throw new BusinessException(
                    "La marca del vehículo es obligatoria"
            );
        }

        if (dto.getEstado() == null) {
            throw new BusinessException(
                    "Debe seleccionar el estado del vehículo"
            );
        }

        if (dto.getTipoVehiculo() == null) {
            throw new BusinessException(
                    "Debe seleccionar el tipo de vehículo"
            );
        }

        if (dto.getNombreUbicacion() == null) {
            throw new BusinessException(
                    "Debe seleccionar la ubicación del vehículo"
            );
        }
    }

    // Valida que la placa sea única
    private void validarPlaca(
            VehiculoDTO dto,
            Long id) {

        if (id == null) {

            if (vehiculoRepository.existsByPlaca(dto.getPlaca())) {

                throw new BusinessException(
                        "La placa del vehículo ya existe"
                );
            }

            return;
        }

        if (vehiculoRepository
                .existsByPlacaAndIdNot(
                        dto.getPlaca(),
                        id)) {

            throw new BusinessException(
                    "La placa pertenece a otro vehículo"
            );
        }
    }

    // Valida si el vehículo puede eliminarse
    private void validarEliminacion(Vehiculo vehiculo) {

        if (vehiculo.getEstado() ==
                com.fireequipmanager.backend.model.enumsVehiculo.EstadoVehiculo.OPERATIVO) {

            throw new BusinessException(
                    "No se puede eliminar un vehículo operativo"
            );
        }

        // Aquí posteriormente validaremos compartimientos.
        // También validaremos equipos y EPP asignados.
    }

    // Obtiene un vehículo
    private Vehiculo obtenerVehiculo(@NonNull Long id) {

        return vehiculoRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                "Vehículo no encontrado"
                        ));
    }

    // Convierte DTO a entidad
    private Vehiculo dtoToEntity(VehiculoDTO dto) {

        Vehiculo vehiculo = new Vehiculo();

        actualizarDatos(vehiculo, dto);

        return vehiculo;
    }
  // Actualiza los datos del vehículo
    private void actualizarDatos(Vehiculo vehiculo, VehiculoDTO dto) {
        vehiculo.setCodigo(dto.getCodigo());
        vehiculo.setNombreVehiculo(dto.getNombreVehiculo());
        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setTipoVehiculo(dto.getTipoVehiculo());
        vehiculo.setEstado(dto.getEstado());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setAnio(dto.getAnio());
        vehiculo.setCapacidadAgua(dto.getCapacidadAgua());
        vehiculo.setCapacidadEspuma(dto.getCapacidadEspuma());
        vehiculo.setObservacion(dto.getObservacion());
        vehiculo.setNumeroMotor(dto.getNumeroMotor());
        vehiculo.setNumeroChasis(dto.getNumeroChasis());

        if (dto.getActivo() != null) {
            vehiculo.setActivo(dto.getActivo());
        }
    }

    // Convierte Entidad a DTO
    private VehiculoDTO entityToDto(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return null;
        }

        return new VehiculoDTO(
                vehiculo.getId(),
                vehiculo.getCodigo(),
                vehiculo.getNombreVehiculo(),
                vehiculo.getPlaca(),
                vehiculo.getMarca(),
                vehiculo.getTipoVehiculo(),
                vehiculo.getEstado(),
                vehiculo.getModelo(),
                vehiculo.getAnio(),
                vehiculo.getCapacidadAgua(),
                vehiculo.getCapacidadEspuma(),
                vehiculo.getObservacion(),
                vehiculo.getActivo(),
                // Mapeo seguro contra nulos del Enum de ubicación
                vehiculo.getUbicacion() != null ? vehiculo.getUbicacion().getNombreUbicacion() : null,
                vehiculo.getNumeroMotor(),
                vehiculo.getNumeroChasis()
        );
    }
}