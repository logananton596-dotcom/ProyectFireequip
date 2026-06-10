package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.EstadoEquipo;
import com.fireequipmanager.backend.repository.EstadoEquipoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fireequipmanager.backend.dto.EstadoEquipoDTO;

import java.util.List;

@Service
@Transactional
public class EstadoEquipoService {

    private final EstadoEquipoRepository repository;

    public EstadoEquipoService(EstadoEquipoRepository repository) {
        this.repository = repository;
    }

    public List<EstadoEquipoDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::convertirAEntityADto)
                .toList();
    }

    public EstadoEquipoDTO crear(EstadoEquipoDTO estadoDTO) {
        // Validar que el nombre no esté vacío y no sea duplicado
        if (estadoDTO.getNombre() == null || estadoDTO.getNombre().isBlank()) {
            throw new BusinessException("El nombre del estado es obligatorio");
        }
        
        // Convertimos a mayúsculas para mantener consistencia en los nombres de estados (ej: OPERATIVO)
        String nombreNormalizado = estadoDTO.getNombre().trim().toUpperCase();

        if (repository.findByNombre(nombreNormalizado).isPresent()) {
            throw new BusinessException("El estado '" + nombreNormalizado + "' ya existe");
        }

        // Mapear DTO a Entidad
        EstadoEquipo estado = new EstadoEquipo();
        estado.setNombre(nombreNormalizado);

        EstadoEquipo guardado = repository.save(estado);
        return convertirAEntityADto(guardado);
    }

    public EstadoEquipoDTO buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("El nombre de búsqueda no puede estar vacío");
        }
        
        return repository.findByNombre(nombre.trim().toUpperCase())
            .map(this::convertirAEntityADto)
            .orElseThrow(() -> new BusinessException("Estado no encontrado: " + nombre));
    }

    // ==========================================
    // MÉTODO PRIVADO DE MAPEO
    // ==========================================
    private EstadoEquipoDTO convertirAEntityADto(EstadoEquipo estado) {
        EstadoEquipoDTO dto = new EstadoEquipoDTO();
        dto.setId(estado.getId());
        dto.setNombre(estado.getNombre());
        return dto;
    }
}