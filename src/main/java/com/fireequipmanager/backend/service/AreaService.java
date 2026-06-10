package com.fireequipmanager.backend.service;


import com.fireequipmanager.backend.dto.AreaDTO;
import com.fireequipmanager.backend.model.Area;
import com.fireequipmanager.backend.repository.AreaRepository;
import com.fireequipmanager.backend.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AreaService {

    private final AreaRepository areaRepository;

    // Inyección por constructor (Igual que en tu EquipoService)
    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    public List<AreaDTO> listarTodas() {
        return areaRepository.findAll().stream()
                .map(this::convertirAEntityADto)
                .toList();
    }

    public AreaDTO buscarPorId(Long id) {
        return areaRepository.findById(id)
                .map(this::convertirAEntityADto)
                .orElseThrow(() -> new BusinessException("Área no encontrada"));
    }

    public AreaDTO crearArea(AreaDTO areaDTO) {
        // RN: Validar nombre único
        if (areaRepository.existsByNombre(areaDTO.getNombre())) {
            throw new BusinessException("Ya existe un área registrada con ese nombre");
        }

        Area area = new Area();
        area.setNombre(areaDTO.getNombre());
        area.setEncargado(areaDTO.getEncargado());
        area.setTelefono(areaDTO.getTelefono());

        Area guardada = areaRepository.save(area);
        return convertirAEntityADto(guardada);
    }

    public AreaDTO actualizarArea(Long id, AreaDTO areaDto) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Área no encontrada"));

        // RN: Validar duplicado en actualización
        if (!area.getNombre().equalsIgnoreCase(areaDto.getNombre()) 
                && areaRepository.existsByNombre(areaDto.getNombre())) {
            throw new BusinessException("Ya existe otra área registrada con ese nombre");
        }

        area.setNombre(areaDto.getNombre());
        area.setEncargado(areaDto.getEncargado());
        area.setTelefono(areaDto.getTelefono());

        Area actualizada = areaRepository.save(area);
        return convertirAEntityADto(actualizada);
    }

    public void eliminar(Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("El área a eliminar no existe"));

        // RN: No permitir eliminar si tiene equipos asociados para evitar errores de BD
        if (area.getEquipos() != null && !area.getEquipos().isEmpty()) {
            throw new BusinessException("No se puede eliminar el área porque tiene equipos asignados");
        }

        areaRepository.delete(area);
    }

    // ==========================================
    // MAPPERS MANUALES
    // ==========================================
    private AreaDTO convertirAEntityADto(Area area) {
        return new AreaDTO(
                area.getId(),
                area.getNombre(),
                area.getEncargado(),
                area.getTelefono()
        );
    }
}