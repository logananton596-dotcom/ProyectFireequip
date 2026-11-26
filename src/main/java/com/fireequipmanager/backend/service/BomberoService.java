package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.dto.BomberoDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Bombero;
import com.fireequipmanager.backend.model.enumsBombero.EstadoBombero;
import com.fireequipmanager.backend.repository.BomberoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.fireequipmanager.backend.model.Ubicacion;
import com.fireequipmanager.backend.model.enumsUbicacion.TipoUbicacion;
import com.fireequipmanager.backend.repository.UbicacionRepository;

import java.util.List;

@Service
@Transactional
public class BomberoService {

    private final BomberoRepository bomberoRepository;
    private final UbicacionRepository ubicacionRepository;

    public BomberoService(BomberoRepository bomberoRepository, UbicacionRepository ubicacionRepository) {
        this.bomberoRepository = bomberoRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    // Lista todos los bomberos
    public List<BomberoDTO> listarTodos() {
        return bomberoRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Lista solo bomberos activos
    public List<BomberoDTO> listarActivos() {
        // CORRECCIÓN: Cambiar findByEstadoIgnoreCase por findByEstado
        return bomberoRepository.findByEstado(EstadoBombero.ACTIVO)
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Busca un bombero por ID
    @SuppressWarnings("null")
    public BomberoDTO buscarPorId(Long id) {
        return bomberoRepository.findById(id)
                .map(this::entityToDto)
                .orElseThrow(() -> new BusinessException("Bombero no encontrado"));
    }

    // Registra un nuevo bombero
    @SuppressWarnings("null") 
    public BomberoDTO crearBombero(BomberoDTO dto) {

        if (bomberoRepository.existsByCodigoCgbvp(dto.getCodigoCgbvp())) {
            throw new BusinessException("El código CGBVP ya se encuentra registrado");
        }

        if (bomberoRepository.existsByDni(dto.getDni())) {
            throw new BusinessException("El DNI ya se encuentra registrado");
        }
        if (dto.getCasilleroId() != null
        && bomberoRepository.existsByUbicacionId(dto.getCasilleroId())) {
            throw new BusinessException(
                    "El casillero seleccionado ya está asignado a otro bombero");
        }

        Ubicacion casillero = obtenerCasillero(dto.getCasilleroId());
        Bombero bombero = dtoToEntity(dto);
         bombero.setUbicacion(casillero);
        Bombero bomberoGuardado = bomberoRepository.save(bombero);
        return entityToDto(bomberoGuardado);
    }

    // Actualiza un bombero existente
    @SuppressWarnings("null") 
    public BomberoDTO actualizarBombero(Long id, BomberoDTO dto) {

        Bombero bombero = bomberoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Bombero no encontrado"));

        if (bomberoRepository.existsByCodigoCgbvpAndIdNot(dto.getCodigoCgbvp(), id)) {
            throw new BusinessException("El código CGBVP ya pertenece a otro bombero");
        }

        if (bomberoRepository.existsByDniAndIdNot(dto.getDni(), id)) {
            throw new BusinessException("El DNI ya pertenece a otro bombero");
        }
        if (dto.getCasilleroId() != null
        && bomberoRepository.existsByUbicacionIdAndIdNot(dto.getCasilleroId(), id)) {
            throw new BusinessException(
                    "El casillero seleccionado ya está asignado a otro bombero");
        }

        Ubicacion casillero = obtenerCasillero(dto.getCasilleroId());
        actualizarDatosBombero(bombero, dto);
        bombero.setUbicacion(casillero);
        Bombero bomberoActualizado = bomberoRepository.save(bombero);
        return entityToDto(bomberoActualizado);
    }

    // Cambia el estado administrativo
    @SuppressWarnings("null")
    public void cambiarEstadoAdministrativo(Long id, EstadoBombero estado) {

        Bombero bombero = bomberoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Bombero no encontrado"));

        bombero.setEstado(estado);

        bomberoRepository.save(bombero);
    }

    // Elimina un bombero sin historial
    @SuppressWarnings("null")
    public void eliminarDefinitivo(Long id) {

        Bombero bombero = bomberoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("El bombero no existe"));

        if (bombero.getAsignaciones() != null && !bombero.getAsignaciones().isEmpty()) {
            throw new BusinessException("No se puede eliminar un bombero con historial de asignaciones");
        }

        bomberoRepository.delete(bombero);
    }

    private Ubicacion obtenerCasillero(Long casilleroId) {
        if (casilleroId == null) {
            return null;
        }
        Ubicacion ubicacion = ubicacionRepository.findById(casilleroId)
                .orElseThrow(() -> new BusinessException(
                        "Casillero no encontrado con id: " + casilleroId));

        if (ubicacion.getTipo() != TipoUbicacion.CASILLERO) {
            throw new BusinessException(
                    "La ubicación seleccionada no es un casillero");
        }

        return ubicacion;
    }
    // Convierte DTO a Entity
    private Bombero dtoToEntity(BomberoDTO dto) {
        Bombero bombero = new Bombero();
        actualizarDatosBombero(bombero, dto);
        return bombero;
    }
    

    // Actualiza los datos de la entidad
    private void actualizarDatosBombero(Bombero bombero, BomberoDTO dto) {

        bombero.setCodigoCgbvp(dto.getCodigoCgbvp());
        bombero.setCompania(dto.getCompania());
        bombero.setFechaIncorporacion(dto.getFechaIncorporacion());

        bombero.setNombre(dto.getNombre());
        bombero.setApellido(dto.getApellido());

        bombero.setDni(dto.getDni());
        bombero.setFechaNacimiento(dto.getFechaNacimiento());

        bombero.setTipoSangre(dto.getTipoSangre());
        bombero.setTalla(dto.getTalla());
        bombero.setPeso(dto.getPeso()); 
        bombero.setTieneCargo(dto.getTieneCargo());
        bombero.setTipoCargo(dto.getTipoCargo());
        bombero.setFechaInicioCargo(dto.getFechaInicioCargo());
        bombero.setFechaFinCargo(dto.getFechaFinCargo());
        bombero.setGrado(dto.getGrado());

        bombero.setTelefono(dto.getTelefono());
        bombero.setTelefonoEmergencia(dto.getTelefonoEmergencia());
        bombero.setCorreo(dto.getCorreo());

        bombero.setLicencia(dto.getLicencia());
        bombero.setTipoLicencia(dto.getTipoLicencia());
        bombero.setTipoVehiculoLicencia(dto.getTipoVehiculoLicencia());
        
        bombero.setLimitacionSalud(dto.getLimitacionSalud());
        
        bombero.setEstado(dto.getEstado());
        bombero.setMotivoEstado(dto.getMotivoEstado());
                // Asignación de Casillero / Ubicación (opcional)
        Long casilleroId = dto.getCasilleroId();
        if (casilleroId != null) {
            Ubicacion ubicacion = ubicacionRepository.findById(java.util.Objects.requireNonNull(casilleroId))
                    .orElseThrow(() -> new BusinessException("El casillero seleccionado no existe"));
            bombero.setUbicacion(ubicacion);
        } else {
            bombero.setUbicacion(null);
        }

    }
    // Convierte Entity a DTO
    private BomberoDTO entityToDto(Bombero bombero) {
        if (bombero == null) {
            return null;
        }

        BomberoDTO dto = new BomberoDTO();
        dto.setId(bombero.getId());
        dto.setCodigoCgbvp(bombero.getCodigoCgbvp());
        dto.setCompania(bombero.getCompania());
        dto.setFechaIncorporacion(bombero.getFechaIncorporacion());
        dto.setNombre(bombero.getNombre());
        dto.setApellido(bombero.getApellido());
        dto.setDni(bombero.getDni());
        dto.setFechaNacimiento(bombero.getFechaNacimiento());
        dto.setTipoSangre(bombero.getTipoSangre());
        dto.setTalla(bombero.getTalla());
        dto.setPeso(bombero.getPeso());
        dto.setTieneCargo(bombero.getTieneCargo());
        dto.setTipoCargo(bombero.getTipoCargo());
        dto.setFechaInicioCargo(bombero.getFechaInicioCargo());
        dto.setFechaFinCargo(bombero.getFechaFinCargo());
        dto.setGrado(bombero.getGrado());
        dto.setTelefono(bombero.getTelefono());
        dto.setTelefonoEmergencia(bombero.getTelefonoEmergencia());
        dto.setCorreo(bombero.getCorreo());
        dto.setLicencia(bombero.getLicencia());
        dto.setTipoLicencia(bombero.getTipoLicencia());
        dto.setTipoVehiculoLicencia(bombero.getTipoVehiculoLicencia());
        dto.setLimitacionSalud(bombero.getLimitacionSalud());
        dto.setEstado(bombero.getEstado());
        dto.setMotivoEstado(bombero.getMotivoEstado());

        if (bombero.getUbicacion() != null) {
            dto.setCasilleroId(bombero.getUbicacion().getId());
            dto.setCasilleroNombre(bombero.getUbicacion().getNombre());
            dto.setCasilleroCodigo(bombero.getUbicacion().getCodigo());
            dto.setCasilleroDisplay(
                bombero.getUbicacion().getCodigo() + " - " + bombero.getUbicacion().getNombre()
            );
        }

        return dto;
    }
}