package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.TipoEquipo;
import com.fireequipmanager.backend.repository.TipoEquipoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fireequipmanager.backend.dto.TipoEquipoDTO;

import java.util.List;

@Service
@Transactional
public class TipoEquipoService {

    private final TipoEquipoRepository repository;

    public TipoEquipoService(TipoEquipoRepository repository) {
        this.repository = repository;
    }

    public List<TipoEquipoDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::convertirAEntityADto)
                .toList();
    }

    public TipoEquipoDTO crear(TipoEquipoDTO tipoDTO) {
        if (tipoDTO.getNombre() == null || tipoDTO.getNombre().isBlank()) {
            throw new BusinessException("El nombre del tipo de equipo es obligatorio");
        }
        
        String nombreNormalizado = tipoDTO.getNombre().trim();

        // Validar duplicidad
        if (repository.existsByNombre(nombreNormalizado)) {
            throw new BusinessException("El tipo de equipo '" + nombreNormalizado + "' ya existe");
        }

        // Mapear DTO a Entidad
        TipoEquipo tipo = new TipoEquipo();
        tipo.setNombre(nombreNormalizado);
        tipo.setDescripcion(tipoDTO.getDescripcion());

        TipoEquipo guardado = repository.save(tipo);
        return convertirAEntityADto(guardado);
    }

    public TipoEquipoDTO buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("El nombre de búsqueda no puede estar vacío");
        }
        
        return repository.findByNombre(nombre.trim())
                .map(this::convertirAEntityADto)
                .orElseThrow(() -> new BusinessException("Tipo de equipo no encontrado: " + nombre));
    }

    // ==========================================
    // MÉTODO PRIVADO DE MAPEO
    // ==========================================
    private TipoEquipoDTO convertirAEntityADto(TipoEquipo tipo) {
        TipoEquipoDTO dto = new TipoEquipoDTO();
        dto.setId(tipo.getId());
        dto.setNombre(tipo.getNombre());
        dto.setDescripcion(tipo.getDescripcion());
        return dto;
    }
}