package com.fireequipmanager.backend.service;


import com.fireequipmanager.backend.dto.UsoEmergenciaDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.TipoEquipo;
import com.fireequipmanager.backend.model.UsoEmergencia;
import com.fireequipmanager.backend.repository.TipoEquipoRepository;
import com.fireequipmanager.backend.repository.UsoEmergenciaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UsoEmergenciaService {

    private final UsoEmergenciaRepository usoEmergenciaRepository;
    private final TipoEquipoRepository tipoEquipoRepository;

    public UsoEmergenciaService(UsoEmergenciaRepository usoEmergenciaRepository, 
                                TipoEquipoRepository tipoEquipoRepository) {
        this.usoEmergenciaRepository = usoEmergenciaRepository;
        this.tipoEquipoRepository = tipoEquipoRepository;
    }

    // LISTAR TODOS LOS USOS DE EMERGENCIA
    public List<UsoEmergenciaDTO> listarTodos() {
        return usoEmergenciaRepository.findAll().stream()
                .map(this::convertirAEntityADto)
                .toList();
    }

    // BUSCAR POR ID
    public UsoEmergenciaDTO buscarPorId(Long id) {
        return usoEmergenciaRepository.findById(id)
                .map(this::convertirAEntityADto)
                .orElseThrow(() -> new BusinessException("Uso de emergencia no encontrado con ID: " + id));
    }

    // CREAR USO DE EMERGENCIA
    public UsoEmergenciaDTO crear(UsoEmergenciaDTO dto) {
        String nombreNormalizado = dto.getNombre().trim();

        // Validar que el nombre no esté duplicado (Asumiendo que tienes este método en tu repository)
        // Puedes comentarlo si tu repository aún no tiene la firma de búsqueda por nombre
        /*
        if (usoEmergenciaRepository.existsByNombre(nombreNormalizado)) {
            throw new BusinessException("El uso de emergencia '" + nombreNormalizado + "' ya está registrado");
        }
        */

        // Buscar y mapear la lista de entidades TipoEquipo asociadas a los IDs provistos
        List<TipoEquipo> tipos = tipoEquipoRepository.findAllById(dto.getTiposPermitidosIds());
        if (tipos.isEmpty() || tipos.size() != dto.getTiposPermitidosIds().size()) {
            throw new BusinessException("Uno o más tipos de equipo especificados no existen en el sistema");
        }

        UsoEmergencia uso = new UsoEmergencia();
        uso.setNombre(nombreNormalizado);
        uso.setTiposPermitidos(tipos);

        UsoEmergencia guardado = usoEmergenciaRepository.save(uso);
        return convertirAEntityADto(guardado);
    }

    // ELIMINAR REGISTRO
    public void eliminar(Long id) {
        if (!usoEmergenciaRepository.existsById(id)) {
            throw new BusinessException("El uso de emergencia que intenta eliminar no existe");
        }
        usoEmergenciaRepository.deleteById(id);
    }

    // ==========================================
    // MÉTODO PRIVADO DE MAPEO (ENTIDAD -> DTO)
    // ==========================================
    private UsoEmergenciaDTO convertirAEntityADto(UsoEmergencia uso) {
        UsoEmergenciaDTO dto = new UsoEmergenciaDTO();
        dto.setId(uso.getId());
        dto.setNombre(uso.getNombre());
        
        // Seteamos la lista de IDs originales que dieron origen a la relación
        if (uso.getTiposPermitidos() != null) {
            List<Long> ids = uso.getTiposPermitidos().stream()
                    .map(TipoEquipo::getId)
                    .toList();
            dto.setTiposPermitidosIds(ids);

            // Mapeamos los objetos simplificados para la sub-clase estática interna del DTO
            List<UsoEmergenciaDTO.TipoEquipoResumenDTO> resumenes = uso.getTiposPermitidos().stream()
                    .map(tipo -> new UsoEmergenciaDTO.TipoEquipoResumenDTO(tipo.getId(), tipo.getNombre()))
                    .toList();
            dto.setTiposPermitidos(resumenes);
        }
        
        return dto;
    }
}