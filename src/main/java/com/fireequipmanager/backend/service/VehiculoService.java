package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.dto.VehiculoDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Vehiculo;
import com.fireequipmanager.backend.model.enumsVehiculo.EstadoVehiculo;
import com.fireequipmanager.backend.model.enumsVehiculo.TipoVehiculo;
import com.fireequipmanager.backend.repository.VehiculoRepository;
import com.fireequipmanager.backend.model.Ubicacion;
import com.fireequipmanager.backend.model.enumsUbicacion.TipoUbicacion;
import com.fireequipmanager.backend.repository.UbicacionRepository;
import jakarta.transaction.Transactional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class VehiculoService {
    private final VehiculoRepository vehiculoRepository;
    private final UbicacionRepository ubicacionRepository;
    public VehiculoService(VehiculoRepository vehiculoRepository,UbicacionRepository ubicacionRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.ubicacionRepository = ubicacionRepository;
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

        Ubicacion ubicacion = obtenerUbicacionNodo(dto.getUbicacionNodoId());

        Vehiculo vehiculo = dtoToEntity(dto);
        vehiculo.setUbicacionNodo(ubicacion);
        vehiculo.setActivo(vehiculo.getEstado() == EstadoVehiculo.OPERATIVO); 
        return entityToDto(vehiculoRepository.save(vehiculo));
    }

    // Actualiza un vehículo
    public VehiculoDTO actualizar(@NonNull Long id, @NonNull VehiculoDTO dto) {
        Vehiculo vehiculo = obtenerVehiculo(id);
        validarDatos(dto);
        validarPlaca(dto, id);

        Ubicacion ubicacion = obtenerUbicacionNodo(dto.getUbicacionNodoId());

        actualizarDatos(vehiculo, dto);
        vehiculo.setUbicacionNodo(ubicacion);
        vehiculo.setActivo(vehiculo.getEstado() == EstadoVehiculo.OPERATIVO); 
        return entityToDto(vehiculoRepository.save(vehiculo));
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
    
    // Cambia el estado del vehículo y sincroniza su disponibilidad (activo = true solo si es OPERATIVO)
    public VehiculoDTO cambiarEstado(@NonNull Long id, @NonNull EstadoVehiculo nuevoEstado) {
        Vehiculo vehiculo = obtenerVehiculo(id);

        vehiculo.setEstado(nuevoEstado);
        vehiculo.setActivo(nuevoEstado == EstadoVehiculo.OPERATIVO);

        return entityToDto(vehiculoRepository.save(vehiculo));
    }


    // Valida los datos básicos
        private void validarDatos(VehiculoDTO dto) {
            if (dto == null) {
                throw new BusinessException("Los datos del vehículo no pueden ser nulos");
            }

            if (dto.getCodigo() == null || dto.getCodigo().isBlank()) {
                throw new BusinessException("El código del vehículo es obligatorio");
            }

            if (dto.getNombreVehiculo() == null || dto.getNombreVehiculo().isBlank()) {
                throw new BusinessException("El nombre del vehículo es obligatorio");
            }

            if (dto.getMarca() == null || dto.getMarca().isBlank()) {
                throw new BusinessException("La marca del vehículo es obligatoria");
            }

            if (dto.getEstado() == null) {
                throw new BusinessException("Debe seleccionar el estado del vehículo");
            }

            if (dto.getTipoVehiculo() == null) {
                throw new BusinessException("Debe seleccionar el tipo de vehículo");
            }

            if (dto.getUbicacionNodoId() == null) {
                throw new BusinessException("Debe seleccionar la ubicación del vehículo");
            }

            // Regla: solo AUTOBOMBA y CISTERNA tienen capacidad de agua
            boolean esContraIncendio = dto.getTipoVehiculo() == TipoVehiculo.AUTOBOMBA
                    || dto.getTipoVehiculo() == TipoVehiculo.CISTERNA;

            if (esContraIncendio) {
                if (dto.getCapacidadAguaLitros() == null || dto.getCapacidadAguaLitros() <= 0) {
                    throw new BusinessException(
                        "Los vehículos contra incendio deben tener capacidad de agua mayor a cero");
                }
            } else {
                if (dto.getCapacidadAguaLitros() != null && dto.getCapacidadAguaLitros() > 0) {
                    throw new BusinessException(
                        "Solo los vehículos contra incendio (AUTOBOMBA/CISTERNA) pueden tener capacidad de agua");
                }
            }
        }
    // Valida que la placa sea única
        private void validarPlaca(VehiculoDTO dto, Long id) {
            String placa = dto.getPlaca();
            if (placa == null || placa.isBlank()) {
                return; // placa opcional
            }

            if (id == null) {
                if (vehiculoRepository.existsByPlaca(placa)) {
                    throw new BusinessException("La placa del vehículo ya existe");
                }
                return;
            }

            if (vehiculoRepository.existsByPlacaAndIdNot(placa, id)) {
                throw new BusinessException("La placa pertenece a otro vehículo");
            }
        }

    private Ubicacion obtenerUbicacionNodo(Long ubicacionId) {
    if (ubicacionId == null) {
        throw new BusinessException("La ubicación del vehículo es obligatoria");
    }
    Ubicacion ubicacion = ubicacionRepository.findById(ubicacionId)
            .orElseThrow(() -> new BusinessException(
                    "Ubicación no encontrada con id: " + ubicacionId));

    if (ubicacion.getTipo() != TipoUbicacion.VEHICULO) {
        throw new BusinessException(
                "La ubicación seleccionada debe ser de tipo VEHICULO");
    }

        return ubicacion;
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
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setAnio(dto.getAnio());
        vehiculo.setTipoVehiculo(dto.getTipoVehiculo());
        vehiculo.setEstado(dto.getEstado());
        vehiculo.setNumeroMotor(dto.getNumeroMotor());
        vehiculo.setNumeroChasis(dto.getNumeroChasis());
        vehiculo.setObservacion(dto.getObservacion());

        vehiculo.setCapacidadAguaLitros(dto.getCapacidadAguaLitros());
        vehiculo.setCapacidadCombustibleGalones(dto.getCapacidadCombustibleGalones());

        vehiculo.setTipoCombustible(dto.getTipoCombustible());
        vehiculo.setTipoAceite(dto.getTipoAceite());
        vehiculo.setIntervaloCambioAceiteKm(dto.getIntervaloCambioAceiteKm());

        vehiculo.setMarcaLlanta(dto.getMarcaLlanta());
        vehiculo.setMedidaLlanta(dto.getMedidaLlanta());
        vehiculo.setTamanoAro(dto.getTamanoAro());
        vehiculo.setTipoLlanta(dto.getTipoLlanta());
        vehiculo.setPresionLlanta(dto.getPresionLlanta());

        vehiculo.setTipoBateria(dto.getTipoBateria());
        vehiculo.setMarcaBateria(dto.getMarcaBateria());
        vehiculo.setAmperajeBateria(dto.getAmperajeBateria());
        vehiculo.setNumeroPlacasBateria(dto.getNumeroPlacasBateria());

        vehiculo.setSoatVigencia(dto.getSoatVigencia());
        vehiculo.setRevisionTecnicaVigencia(dto.getRevisionTecnicaVigencia());
        vehiculo.setTiempoServicioAnios(dto.getTiempoServicioAnios());
        vehiculo.setFechaIncorporacion(dto.getFechaIncorporacion());

        // Regla centralizada: activo solo es true si el estado es OPERATIVO
        vehiculo.setActivo(dto.getEstado() == EstadoVehiculo.OPERATIVO);
    }

        // Convierte Entidad a DTO
    private VehiculoDTO entityToDto(Vehiculo vehiculo) {
        if (vehiculo == null) return null;

        return new VehiculoDTO(
            vehiculo.getId(),
            vehiculo.getCodigo(),
            vehiculo.getNombreVehiculo(),
            vehiculo.getPlaca(),
            vehiculo.getMarca(),
            vehiculo.getModelo(),
            vehiculo.getAnio(),
            vehiculo.getTipoVehiculo(),
            vehiculo.getEstado(),
            vehiculo.getNumeroMotor(),
            vehiculo.getNumeroChasis(),
            vehiculo.getObservacion(),
            vehiculo.getActivo(),
            vehiculo.getCapacidadAguaLitros(),
            vehiculo.getCapacidadCombustibleGalones(),
            vehiculo.getTipoCombustible(),
            vehiculo.getTipoAceite(),
            vehiculo.getIntervaloCambioAceiteKm(),
            vehiculo.getMarcaLlanta(),
            vehiculo.getMedidaLlanta(),
            vehiculo.getTamanoAro(),
            vehiculo.getTipoLlanta(),
            vehiculo.getPresionLlanta(),
            vehiculo.getTipoBateria(),
            vehiculo.getMarcaBateria(),
            vehiculo.getAmperajeBateria(),
            vehiculo.getNumeroPlacasBateria(),
            vehiculo.getSoatVigencia(),
            vehiculo.getRevisionTecnicaVigencia(),
            vehiculo.getTiempoServicioAnios(),
            vehiculo.getFechaIncorporacion(),
            vehiculo.getUbicacionNodo() != null ? vehiculo.getUbicacionNodo().getId() : null,
            vehiculo.getUbicacionNodo() != null ? vehiculo.getUbicacionNodo().getNombre() : null,
            vehiculo.getUbicacionNodo() != null ? vehiculo.getUbicacionNodo().getCodigo() : null
        );
    }
}