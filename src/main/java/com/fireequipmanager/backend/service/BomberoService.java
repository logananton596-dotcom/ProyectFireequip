package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.dto.BomberoDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Bombero;
import com.fireequipmanager.backend.model.enumsBombero.EstadoBombero;
import com.fireequipmanager.backend.repository.BomberoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class BomberoService {

    private final BomberoRepository bomberoRepository;

    public BomberoService(BomberoRepository bomberoRepository) {
        this.bomberoRepository = bomberoRepository;
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

        Bombero bombero = dtoToEntity(dto);
    
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

        actualizarDatosBombero(bombero, dto);

        // SOLUCIÓN: Almacenar en variable local y validar nulidad antes de convertir
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
    }

    // Convierte Entity a DTO
    private BomberoDTO entityToDto(Bombero bombero) {

        return new BomberoDTO(
                bombero.getId(),
                bombero.getCodigoCgbvp(),
                bombero.getCompania(),
                bombero.getFechaIncorporacion(),
                bombero.getNombre(),
                bombero.getApellido(),
                bombero.getDni(),
                bombero.getFechaNacimiento(),
                bombero.getTipoSangre(),
                bombero.getTalla(),
                bombero.getPeso(),
                bombero.getTieneCargo(),
                bombero.getTipoCargo(),
                bombero.getFechaInicioCargo(),
                bombero.getFechaFinCargo(),
                bombero.getGrado(),
                bombero.getTelefono(),
                bombero.getTelefonoEmergencia(),
                bombero.getCorreo(),
                bombero.getLicencia(),
                bombero.getTipoLicencia(),
                bombero.getTipoVehiculoLicencia(),
                bombero.getLimitacionSalud(),
                bombero.getEstado(),
                bombero.getMotivoEstado()
            );
    }

}