package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.dto.AsignacionDTO;
import com.fireequipmanager.backend.dto.BomberoDTO;
import com.fireequipmanager.backend.dto.CompartimientoVehiculoDTO;
import com.fireequipmanager.backend.dto.EppDTO;
import com.fireequipmanager.backend.dto.EquipoDTO;
import com.fireequipmanager.backend.dto.UbicacionDTO;
import com.fireequipmanager.backend.dto.VehiculoDTO;
import com.fireequipmanager.backend.model.Asignacion;
import com.fireequipmanager.backend.model.Bombero;
import com.fireequipmanager.backend.model.CompartimientoVehiculo;
import com.fireequipmanager.backend.model.Epp;
import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.model.Ubicacion;
import com.fireequipmanager.backend.model.Vehiculo;
import com.fireequipmanager.backend.model.enumsAsignacion.EstadoAsignacion;
import com.fireequipmanager.backend.model.enumsAsignacion.TipoDestinoAsignacion;
import com.fireequipmanager.backend.repository.AsignacionRepository;
import com.fireequipmanager.backend.repository.BomberoRepository;
import com.fireequipmanager.backend.repository.CompartimientoVehiculoRepository;
import com.fireequipmanager.backend.repository.EppRepository;
import com.fireequipmanager.backend.repository.EquipoRepository;
import com.fireequipmanager.backend.repository.UbicacionRepository;
import com.fireequipmanager.backend.repository.VehiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final EquipoRepository equipoRepository;
    private final EppRepository eppRepository;
    private final BomberoRepository bomberoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final CompartimientoVehiculoRepository compartimientoVehiculoRepository;

    // ==========================
    // CONSULTAS (READ)
    // ==========================

    @Transactional(readOnly = true)
    public List<AsignacionDTO> listarTodos() {
        return asignacionRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AsignacionDTO> listarActivas() {
        return asignacionRepository.findByActivoTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AsignacionDTO> listarPorEstado(EstadoAsignacion estado) {
        return asignacionRepository.findByEstadoAndActivoTrue(estado)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AsignacionDTO buscarPorId(Long id) {
        Asignacion asignacion = asignacionRepository.findById(Objects.requireNonNull(id, "El ID de la asignación no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada con ID: " + id));
        return convertirADTO(asignacion);
    }

    @Transactional(readOnly = true)
    public List<AsignacionDTO> listarPorBombero(Long bomberoId) {
        return asignacionRepository.findByBomberoId(bomberoId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AsignacionDTO> listarPorEquipo(Long equipoId) {
        return asignacionRepository.findByEquipoId(equipoId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AsignacionDTO> listarPorEpp(Long eppId) {
        return asignacionRepository.findByEppId(eppId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AsignacionDTO> listarPorVehiculo(Long vehiculoId) {
        return asignacionRepository.findByVehiculoId(vehiculoId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AsignacionDTO> listarPorUbicacion(Long ubicacionId) {
        return asignacionRepository.findByUbicacionId(ubicacionId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ==========================
    // CREACIÓN
    // ==========================

    public AsignacionDTO crear(AsignacionDTO dto) {
        log.info("Creando nueva asignación: {}", dto);

        // 1. Validar recurso (Equipo XOR EPP)
        validarRecurso(dto);

        // 2. Validar destino (Bombero XOR Vehículo XOR Ubicación)
        validarDestino(dto);

        // 3. Validar consistencia del compartimiento
        validarCompartimiento(dto);

        // 4. Validar disponibilidad del recurso
        validarDisponibilidadRecurso(dto);

        // 5. Obtener y validar las entidades
        Equipo equipo = null;
        Epp epp = null;
        Bombero bombero = null;
        Vehiculo vehiculo = null;
        Ubicacion ubicacion = null;
        CompartimientoVehiculo compartimiento = null;

        if (dto.getEquipoId() != null) {
            equipo = equipoRepository.findById(Objects.requireNonNull(dto.getEquipoId(), "El ID del equipo no puede ser nulo"))
                    .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + dto.getEquipoId()));
        }

        if (dto.getEppId() != null) {
            epp = eppRepository.findById(Objects.requireNonNull(dto.getEppId(), "El ID del EPP no puede ser nulo"))
                    .orElseThrow(() -> new EntityNotFoundException("EPP no encontrado con ID: " + dto.getEppId()));
        }

        if (dto.getTipoDestino() == TipoDestinoAsignacion.BOMBERO) {
            bombero = bomberoRepository.findById(Objects.requireNonNull(dto.getBomberoId(), "El ID del bombero no puede ser nulo"))
                    .orElseThrow(() -> new EntityNotFoundException("Bombero no encontrado con ID: " + dto.getBomberoId()));
        } else if (dto.getTipoDestino() == TipoDestinoAsignacion.VEHICULO) {
            vehiculo = vehiculoRepository.findById(Objects.requireNonNull(dto.getVehiculoId(), "El ID del vehículo no puede ser nulo"))
                    .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con ID: " + dto.getVehiculoId()));

            if (dto.getCompartimientoId() != null) {
                compartimiento = compartimientoVehiculoRepository.findById(Objects.requireNonNull(dto.getCompartimientoId(), "El ID del compartimiento no puede ser nulo"))
                        .orElseThrow(() -> new EntityNotFoundException("Compartimiento no encontrado con ID: " + dto.getCompartimientoId()));

                // Validar que el compartimiento pertenezca al vehículo
                if (!compartimiento.getVehiculo().getId().equals(vehiculo.getId())) {
                    throw new IllegalArgumentException("El compartimiento no pertenece al vehículo seleccionado");
                }
            }
        } else if (dto.getTipoDestino() == TipoDestinoAsignacion.UBICACION) {
            ubicacion = ubicacionRepository.findById(Objects.requireNonNull(dto.getUbicacionId(), "El ID de la ubicación no puede ser nulo"))
                    .orElseThrow(() -> new EntityNotFoundException("Ubicación no encontrada con ID: " + dto.getUbicacionId()));
        }

        // 6. Crear la asignación
        Asignacion asignacion = new Asignacion();
        asignacion.setEquipo(equipo);
        asignacion.setEpp(epp);
        asignacion.setTipoDestino(dto.getTipoDestino());
        asignacion.setBombero(bombero);
        asignacion.setVehiculo(vehiculo);
        asignacion.setUbicacion(ubicacion);
        asignacion.setCompartimiento(compartimiento);
        asignacion.setFechaAsignacion(dto.getFechaAsignacion() != null ? dto.getFechaAsignacion() : LocalDate.now());
        asignacion.setFechaDevolucion(dto.getFechaDevolucion());
        asignacion.setFechaFin(dto.getFechaFin());
        asignacion.setEstado(EstadoAsignacion.ACTIVA); // Siempre ACTIVA al crear
        asignacion.setObservaciones(dto.getObservaciones());
        asignacion.setActivo(true); // Siempre true al crear

        // 7. Guardar la asignación
        Asignacion asignacionGuardada = asignacionRepository.save(asignacion);

        // 8. Actualizar ubicación del recurso
        actualizarUbicacionRecurso(asignacionGuardada);

        log.info("Asignación creada exitosamente con ID: {}", asignacionGuardada.getId());
        return convertirADTO(asignacionGuardada);
    }

    // ==========================
    // ACTUALIZACIÓN
    // ==========================

    public AsignacionDTO actualizar(Long id, AsignacionDTO dto) {
        log.info("Actualizando asignación con ID: {}", id);

        Asignacion asignacionExistente = asignacionRepository.findById(Objects.requireNonNull(id, "El ID de la asignación no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada con ID: " + id));

        // No permitir actualizar asignaciones devueltas o inactivas
        if (asignacionExistente.getEstado() == EstadoAsignacion.DEVUELTA || !asignacionExistente.getActivo()) {
            throw new IllegalStateException("No se puede actualizar una asignación devuelta o inactiva");
        }

        // Validar que el recurso no haya cambiado (opcional - depende de requisitos)
        // Si cambia el recurso, debemos liberar el anterior y asignar el nuevo
        boolean recursoCambiado = false;
        if (dto.getEquipoId() != null && asignacionExistente.getEquipo() != null) {
            if (!dto.getEquipoId().equals(asignacionExistente.getEquipo().getId())) {
                recursoCambiado = true;
            }
        } else if (dto.getEppId() != null && asignacionExistente.getEpp() != null) {
            if (!dto.getEppId().equals(asignacionExistente.getEpp().getId())) {
                recursoCambiado = true;
            }
        } else if ((dto.getEquipoId() != null && asignacionExistente.getEpp() != null) ||
                   (dto.getEppId() != null && asignacionExistente.getEquipo() != null)) {
            recursoCambiado = true;
        }

        if (recursoCambiado) {
            // Liberar el recurso anterior (poner ubicación por defecto o null)
            liberarRecurso(asignacionExistente);
            
            // Validar disponibilidad del nuevo recurso
            validarDisponibilidadRecurso(dto);
        }

        // 1. Validar recurso
        validarRecurso(dto);

        // 2. Validar destino
        validarDestino(dto);

        // 3. Validar consistencia del compartimiento
        validarCompartimiento(dto);

        // 4. Obtener y validar las entidades actualizadas
        Equipo equipo = null;
        Epp epp = null;
        Bombero bombero = null;
        Vehiculo vehiculo = null;
        Ubicacion ubicacion = null;
        CompartimientoVehiculo compartimiento = null;

        if (dto.getEquipoId() != null) {
            equipo = equipoRepository.findById(Objects.requireNonNull(dto.getEquipoId(), "El ID del equipo no puede ser nulo"))
                    .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + dto.getEquipoId()));
        }

        if (dto.getEppId() != null) {
            epp = eppRepository.findById(Objects.requireNonNull(dto.getEppId(), "El ID del EPP no puede ser nulo"))
                    .orElseThrow(() -> new EntityNotFoundException("EPP no encontrado con ID: " + dto.getEppId()));
        }

        if (dto.getTipoDestino() == TipoDestinoAsignacion.BOMBERO) {
            bombero = bomberoRepository.findById(Objects.requireNonNull(dto.getBomberoId(), "El ID del bombero no puede ser nulo"))
                    .orElseThrow(() -> new EntityNotFoundException("Bombero no encontrado con ID: " + dto.getBomberoId()));
        } else if (dto.getTipoDestino() == TipoDestinoAsignacion.VEHICULO) {
            vehiculo = vehiculoRepository.findById(Objects.requireNonNull(dto.getVehiculoId(), "El ID del vehículo no puede ser nulo"))
                    .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con ID: " + dto.getVehiculoId()));

            if (dto.getCompartimientoId() != null) {
                compartimiento = compartimientoVehiculoRepository.findById(Objects.requireNonNull(dto.getCompartimientoId(), "El ID del compartimiento no puede ser nulo"))
                        .orElseThrow(() -> new EntityNotFoundException("Compartimiento no encontrado con ID: " + dto.getCompartimientoId()));

                if (!compartimiento.getVehiculo().getId().equals(vehiculo.getId())) {
                    throw new IllegalArgumentException("El compartimiento no pertenece al vehículo seleccionado");
                }
            }
        } else if (dto.getTipoDestino() == TipoDestinoAsignacion.UBICACION) {
            ubicacion = ubicacionRepository.findById(Objects.requireNonNull(dto.getUbicacionId(), "El ID de la ubicación no puede ser nulo"))
                    .orElseThrow(() -> new EntityNotFoundException("Ubicación no encontrada con ID: " + dto.getUbicacionId()));
        }

        // 5. Actualizar la asignación
        asignacionExistente.setEquipo(equipo);
        asignacionExistente.setEpp(epp);
        asignacionExistente.setTipoDestino(dto.getTipoDestino());
        asignacionExistente.setBombero(bombero);
        asignacionExistente.setVehiculo(vehiculo);
        asignacionExistente.setUbicacion(ubicacion);
        asignacionExistente.setCompartimiento(compartimiento);
        asignacionExistente.setFechaAsignacion(dto.getFechaAsignacion());
        asignacionExistente.setFechaDevolucion(dto.getFechaDevolucion());
        asignacionExistente.setFechaFin(dto.getFechaFin());
        asignacionExistente.setObservaciones(dto.getObservaciones());

        // 6. Guardar la asignación
        Asignacion asignacionActualizada = asignacionRepository.save(asignacionExistente);

        // 7. Actualizar ubicación del recurso
        actualizarUbicacionRecurso(asignacionActualizada);

        log.info("Asignación actualizada exitosamente con ID: {}", asignacionActualizada.getId());
        return convertirADTO(asignacionActualizada);
    }

    // ==========================
    // DEVOLUCIÓN
    // ==========================

    public AsignacionDTO devolver(Long id, LocalDate fechaDevolucion, String observaciones) {
        log.info("Devolviendo asignación con ID: {}", id);

        Asignacion asignacion = asignacionRepository.findById(Objects.requireNonNull(id, "El ID de la asignación no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada con ID: " + id));

        if (asignacion.getEstado() == EstadoAsignacion.DEVUELTA) {
            throw new IllegalStateException("La asignación ya está devuelta");
        }

        // Actualizar la asignación
        asignacion.setEstado(EstadoAsignacion.DEVUELTA);
        asignacion.setFechaDevolucion(fechaDevolucion != null ? fechaDevolucion : LocalDate.now());
        if (observaciones != null) {
            String obsActual = asignacion.getObservaciones() != null ? asignacion.getObservaciones() : "";
            asignacion.setObservaciones(obsActual + " | DEVOLUCIÓN: " + observaciones);
        }

        Asignacion asignacionDevuelta = asignacionRepository.save(asignacion);

        // Liberar el recurso (actualizar ubicación a null o ubicación por defecto)
        liberarRecurso(asignacionDevuelta);

        log.info("Asignación devuelta exitosamente con ID: {}", asignacionDevuelta.getId());
        return convertirADTO(asignacionDevuelta);
    }

    // ==========================
    // ELIMINACIÓN (LÓGICA)
    // ==========================

    public void eliminar(Long id) {
        log.info("Eliminando (lógicamente) asignación con ID: {}", id);

        Asignacion asignacion = asignacionRepository.findById(Objects.requireNonNull(id, "El ID de la asignación no puede ser nulo"))
                .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada con ID: " + id));

        // Si está activa, primero liberar el recurso
        if (asignacion.getEstado() == EstadoAsignacion.ACTIVA) {
            liberarRecurso(asignacion);
        }

        asignacion.setActivo(false);
        asignacionRepository.save(asignacion);

        log.info("Asignación eliminada lógicamente con ID: {}", id);
    }

    // ==========================
    // VALIDACIONES PRIVADAS
    // ==========================

    private void validarRecurso(AsignacionDTO dto) {
        boolean tieneEquipo = dto.getEquipoId() != null;
        boolean tieneEpp = dto.getEppId() != null;

        if (tieneEquipo && tieneEpp) {
            throw new IllegalArgumentException("No se puede asignar un Equipo y un EPP simultáneamente. Debe seleccionar solo uno.");
        }

        if (!tieneEquipo && !tieneEpp) {
            throw new IllegalArgumentException("Debe seleccionar un Equipo o un EPP para la asignación.");
        }
    }

    private void validarDestino(AsignacionDTO dto) {
        TipoDestinoAsignacion tipoDestino = dto.getTipoDestino();

        if (tipoDestino == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de destino.");
        }

        switch (tipoDestino) {
            case BOMBERO:
                if (dto.getBomberoId() == null) {
                    throw new IllegalArgumentException("Debe seleccionar un bombero cuando el destino es BOMBERO.");
                }
                if (dto.getVehiculoId() != null || dto.getUbicacionId() != null) {
                    throw new IllegalArgumentException("Cuando el destino es BOMBERO, no debe seleccionar vehículo ni ubicación.");
                }
                break;

            case VEHICULO:
                if (dto.getVehiculoId() == null) {
                    throw new IllegalArgumentException("Debe seleccionar un vehículo cuando el destino es VEHICULO.");
                }
                if (dto.getBomberoId() != null || dto.getUbicacionId() != null) {
                    throw new IllegalArgumentException("Cuando el destino es VEHICULO, no debe seleccionar bombero ni ubicación.");
                }
                break;

            case UBICACION:
                if (dto.getUbicacionId() == null) {
                    throw new IllegalArgumentException("Debe seleccionar una ubicación cuando el destino es UBICACION.");
                }
                if (dto.getBomberoId() != null || dto.getVehiculoId() != null) {
                    throw new IllegalArgumentException("Cuando el destino es UBICACION, no debe seleccionar bombero ni vehículo.");
                }
                break;

            default:
                throw new IllegalArgumentException("Tipo de destino no válido: " + tipoDestino);
        }
    }

    private void validarCompartimiento(AsignacionDTO dto) {
        if (dto.getCompartimientoId() != null && dto.getTipoDestino() != TipoDestinoAsignacion.VEHICULO) {
            throw new IllegalArgumentException("El compartimiento solo puede especificarse cuando el destino es VEHICULO.");
        }
    }

    private void validarDisponibilidadRecurso(AsignacionDTO dto) {
        if (dto.getEquipoId() != null) {
            boolean equipoAsignado = asignacionRepository.existsByEquipoIdAndEstado(
                    dto.getEquipoId(),
                    EstadoAsignacion.ACTIVA
            );
            if (equipoAsignado) {
                throw new IllegalStateException("El equipo ya está asignado actualmente.");
            }
        }

        if (dto.getEppId() != null) {
            boolean eppAsignado = asignacionRepository.existsByEppIdAndEstado(
                    dto.getEppId(),
                    EstadoAsignacion.ACTIVA
            );
            if (eppAsignado) {
                throw new IllegalStateException("El EPP ya está asignado actualmente.");
            }
        }
    }

    // ==========================
    // ACTUALIZACIÓN DE UBICACIÓN
    // ==========================

    private void actualizarUbicacionRecurso(Asignacion asignacion) {
        Equipo equipo = asignacion.getEquipo();
        Epp epp = asignacion.getEpp();

        if (equipo != null) {
            Ubicacion nuevaUbicacion = obtenerUbicacionDesdeAsignacion(asignacion);
            equipo.setUbicacion(nuevaUbicacion);
            equipoRepository.save(equipo);
            log.info("Ubicación del equipo {} actualizada a: {}", equipo.getId(), nuevaUbicacion != null ? nuevaUbicacion.getNombreUbicacion() : "null");
        }

        if (epp != null) {
            Ubicacion nuevaUbicacion = obtenerUbicacionDesdeAsignacion(asignacion);
            epp.setUbicacion(nuevaUbicacion);
            eppRepository.save(epp);
            log.info("Ubicación del EPP {} actualizada a: {}", epp.getId(), nuevaUbicacion != null ? nuevaUbicacion.getNombreUbicacion() : "null");
        }
    }

    private Ubicacion obtenerUbicacionDesdeAsignacion(Asignacion asignacion) {
        TipoDestinoAsignacion tipoDestino = asignacion.getTipoDestino();

        switch (tipoDestino) {
            case BOMBERO:
                Bombero bombero = asignacion.getBombero();
                if (bombero != null && bombero.getUbicacion() != null) {
                    return bombero.getUbicacion();
                }
                // Si el bombero no tiene ubicación, devolver null
                return null;

            case VEHICULO:
                Vehiculo vehiculo = asignacion.getVehiculo();
                if (vehiculo != null) {
                    // Si tiene compartimiento, usamos la ubicación del compartimiento
                    if (asignacion.getCompartimiento() != null) {
                        return asignacion.getCompartimiento().getUbicacion();
                    }
                    // Si no tiene compartimiento, usamos la ubicación del vehículo
                    return vehiculo.getUbicacion();
                }
                return null;

            case UBICACION:
                return asignacion.getUbicacion();

            default:
                return null;
        }
    }

    private void liberarRecurso(Asignacion asignacion) {
        Equipo equipo = asignacion.getEquipo();
        Epp epp = asignacion.getEpp();

        // Podemos establecer la ubicación a null o a una ubicación por defecto
        // Aquí lo dejamos como null, indicando que no tiene ubicación actual
        if (equipo != null) {
            equipo.setUbicacion(null);
            equipoRepository.save(equipo);
            log.info("Ubicación del equipo {} liberada", equipo.getId());
        }

        if (epp != null) {
            epp.setUbicacion(null);
            eppRepository.save(epp);
            log.info("Ubicación del EPP {} liberada", epp.getId());
        }
    }

    // ==========================
    // CONVERSIÓN A DTO
    // ==========================

    private AsignacionDTO convertirADTO(Asignacion asignacion) {
        AsignacionDTO dto = new AsignacionDTO();

        dto.setId(asignacion.getId());

        // Recurso
        if (asignacion.getEquipo() != null) {
            dto.setEquipoId(asignacion.getEquipo().getId());
            // Convertir Equipo a EquipoDTO (simplificado)
            EquipoDTO equipoDTO = new EquipoDTO();
            equipoDTO.setId(asignacion.getEquipo().getId());
            equipoDTO.setNombreEquipo(asignacion.getEquipo().getNombreEquipo());
            equipoDTO.setCodigoCgbvp(asignacion.getEquipo().getCodigoCgbvp());
            dto.setEquipo(equipoDTO);
        }

        if (asignacion.getEpp() != null) {
            dto.setEppId(asignacion.getEpp().getId());
            EppDTO eppDTO = new EppDTO();
            eppDTO.setId(asignacion.getEpp().getId());
            eppDTO.setNombreEpp(asignacion.getEpp().getNombreEpp());
            eppDTO.setCodigoCgbvp(asignacion.getEpp().getCodigoCgbvp());
            dto.setEpp(eppDTO);
        }

        // Destino
        dto.setTipoDestino(asignacion.getTipoDestino());

        if (asignacion.getBombero() != null) {
            dto.setBomberoId(asignacion.getBombero().getId());
            BomberoDTO bomberoDTO = new BomberoDTO();
            bomberoDTO.setId(asignacion.getBombero().getId());
            bomberoDTO.setNombre(asignacion.getBombero().getNombre());
            bomberoDTO.setApellido(asignacion.getBombero().getApellido());
            dto.setBombero(bomberoDTO);
        }

        if (asignacion.getVehiculo() != null) {
            dto.setVehiculoId(asignacion.getVehiculo().getId());
            VehiculoDTO vehiculoDTO = new VehiculoDTO();
            vehiculoDTO.setId(asignacion.getVehiculo().getId());
            vehiculoDTO.setNombreVehiculo(asignacion.getVehiculo().getNombreVehiculo());
            vehiculoDTO.setCodigo(asignacion.getVehiculo().getCodigo());
            dto.setVehiculo(vehiculoDTO);
        }

        if (asignacion.getUbicacion() != null) {
            dto.setUbicacionId(asignacion.getUbicacion().getId());
            UbicacionDTO ubicacionDTO = new UbicacionDTO();
            ubicacionDTO.setId(asignacion.getUbicacion().getId());
            ubicacionDTO.setNombreUbicacion(asignacion.getUbicacion().getNombreUbicacion());
            ubicacionDTO.setCodigo(asignacion.getUbicacion().getCodigo());
            dto.setUbicacion(ubicacionDTO);
            dto.setCodigoUbicacion(asignacion.getUbicacion().getCodigo());
            dto.setNombreUbicacion(asignacion.getUbicacion().getNombreUbicacion());
        }

        if (asignacion.getCompartimiento() != null) {
            dto.setCompartimientoId(asignacion.getCompartimiento().getId());
            CompartimientoVehiculoDTO compartimientoDTO = new CompartimientoVehiculoDTO();
            compartimientoDTO.setId(asignacion.getCompartimiento().getId());
            compartimientoDTO.setCodigo(asignacion.getCompartimiento().getCodigo());
            dto.setCompartimiento(compartimientoDTO);
        }

        // Fechas
        dto.setFechaAsignacion(asignacion.getFechaAsignacion());
        dto.setFechaDevolucion(asignacion.getFechaDevolucion());
        dto.setFechaFin(asignacion.getFechaFin());

        // Estado
        dto.setEstado(asignacion.getEstado());
        dto.setObservaciones(asignacion.getObservaciones());
        dto.setActivo(asignacion.getActivo());

        return dto;
    }
}