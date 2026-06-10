package com.fireequipmanager.backend.service;


import com.fireequipmanager.backend.dto.BomberoDTO;
import com.fireequipmanager.backend.model.Bombero;
import com.fireequipmanager.backend.repository.BomberoRepository;
import com.fireequipmanager.backend.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class BomberoService {

    private final BomberoRepository bomberoRepository;

    // Inyección por constructor alineado a tu arquitectura
    public BomberoService(BomberoRepository bomberoRepository) {
        this.bomberoRepository = bomberoRepository;
    }

    public List<BomberoDTO> listarTodos() {
        return bomberoRepository.findAll().stream()
                .map(this::convertirAEntityADto)
                .toList();
    }

    // Filtro clave para llenar el desplegable del formulario de asignaciones
    public List<BomberoDTO> listarActivos() {
        return bomberoRepository.findByActivoTrue().stream()
                .map(this::convertirAEntityADto)
                .toList();
    }

    public BomberoDTO buscarPorId(Long id) {
        return bomberoRepository.findById(id)
                .map(this::convertirAEntityADto)
                .orElseThrow(() -> new BusinessException("Bombero no encontrado"));
    }

    public BomberoDTO crearBombero(BomberoDTO bomberoDTO) {
        // RN: El código del bombero (Placa/DNI) debe ser único
        if (bomberoRepository.existsByCodigo(bomberoDTO.getCodigo())) {
            throw new BusinessException("El código de bombero ya se encuentra registrado");
        }

        Bombero bombero = new Bombero();
        bombero.setCodigo(bomberoDTO.getCodigo());
        bombero.setNombre(bomberoDTO.getNombre());
        bombero.setGrado(bomberoDTO.getGrado());
        bombero.setTelefono(bomberoDTO.getTelefono());
        bombero.setActivo(true); // Todo bombero nuevo inicia activo

        Bombero guardado = bomberoRepository.save(bombero);
        return convertirAEntityADto(guardado);
    }

    public BomberoDTO actualizarBombero(Long id, BomberoDTO bomberoDto) {
        Bombero bombero = bomberoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Bombero no encontrado"));

        // RN: Validar duplicado de código ignorando el registro actual
        if (bomberoRepository.existsByCodigoAndIdNot(bomberoDto.getCodigo(), id)) {
            throw new BusinessException("El código de bombero ya pertenece a otro efectivo");
        }

        bombero.setCodigo(bomberoDto.getCodigo());
        bombero.setNombre(bomberoDto.getNombre());
        bombero.setGrado(bomberoDto.getGrado());
        bombero.setTelefono(bomberoDto.getTelefono());
        bombero.setActivo(bomberoDto.isActivo()); // Permite activar o desactivar al editar

        Bombero actualizado = bomberoRepository.save(bombero);
        return convertirAEntityADto(actualizado);
    }

    // Borrado lógico (Desactivación) para mantener trazabilidad histórica
    public void cambiarEstadoActivo(Long id, boolean estado) {
        Bombero bombero = bomberoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Bombero no encontrado"));
        
        bombero.setActivo(estado);
        bomberoRepository.save(bombero);
    }

    public void eliminarDefinitivo(Long id) {
        Bombero bombero = bomberoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("El bombero a eliminar no existe"));

        // RN: No permitir borrar físicamente si ya tiene equipos asignados en el historial
        if (bombero.getAsignaciones() != null && !bombero.getAsignaciones().isEmpty()) {
            throw new BusinessException("No se puede eliminar el registro del bombero porque cuenta con historial de asignaciones. Considere desactivarlo.");
        }

        bomberoRepository.delete(bombero);
    }

    // ==========================================
    // MAPPER MANUAL
    // ==========================================
    private BomberoDTO convertirAEntityADto(Bombero bombero) {
        return new BomberoDTO(
                bombero.getId(),
                bombero.getCodigo(),
                bombero.getNombre(),
                bombero.getGrado(),
                bombero.getTelefono(),
                bombero.isActivo()
        );
    }
}