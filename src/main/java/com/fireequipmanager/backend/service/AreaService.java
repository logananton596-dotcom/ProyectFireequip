package com.fireequipmanager.backend.service;


import com.fireequipmanager.backend.dto.AreaDTO;
import com.fireequipmanager.backend.model.Area;
import com.fireequipmanager.backend.model.Bombero;
import com.fireequipmanager.backend.model.enumsBombero.EstadoBombero;
import com.fireequipmanager.backend.repository.AreaRepository;
import com.fireequipmanager.backend.repository.BomberoRepository;
import org.springframework.lang.NonNull; 
import java.util.Objects;

import com.fireequipmanager.backend.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AreaService {

    private final AreaRepository areaRepository;
    private final BomberoRepository bomberoRepository;

    // Inyección por constructor (Igual que en tu EquipoService)
    public AreaService(AreaRepository areaRepository, BomberoRepository bomberoRepository) {
        this.areaRepository = areaRepository;
        this.bomberoRepository = bomberoRepository;
    }

    // Lista todas las áreas
    public List<AreaDTO> listarTodas() {
        return areaRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Busca un área por ID
    public AreaDTO buscarPorId(@NonNull Long id) {
        return areaRepository.findById(id)
                .map(this::entityToDto)
                .orElseThrow(() -> new BusinessException("Área no encontrada"));
    }

  // ========== NUEVO: MÉTODO DE VALIDACIÓN DE ENCARGADO ÚNICO ==========

    /**
     * Valida que un bombero no esté asignado a ninguna otra área activa
     */
    private void validarEncargadoUnico(Long bomberoId, Long idIgnorar, String rol) {
        if (bomberoId == null) return;

        // Obtener el bombero para el mensaje
        Bombero bombero = bomberoRepository.findById(bomberoId)
                .orElseThrow(() -> new BusinessException("El bombero no existe"));

        // Verificar si ya está en otra área activa (excluyendo la actual si es edición)
        boolean yaAsignado = areaRepository.existsBomberoEnAlgunaArea(bomberoId, idIgnorar);

        if (yaAsignado) {
            // Determinar en qué rol está asignado para un mensaje más claro
            boolean esPrincipal = areaRepository.existsBomberoComoPrincipalEnArea(bomberoId, idIgnorar);
            boolean esSecundario = areaRepository.existsBomberoComoSecundarioEnArea(bomberoId, idIgnorar);
            
            String rolAsignado = "";
            if (esPrincipal && esSecundario) {
                rolAsignado = "como encargado principal y secundario";
            } else if (esPrincipal) {
                rolAsignado = "como encargado principal";
            } else if (esSecundario) {
                rolAsignado = "como encargado secundario";
            } else {
                rolAsignado = "en otra área";
            }

            String nombreCompleto = bombero.getNombre() + " " + bombero.getApellido();
            throw new BusinessException(
                String.format("El bombero %s (%s) ya está asignado %s en otra área activa. " +
                              "Un bombero solo puede ser encargado de un área.",
                              nombreCompleto, bombero.getCodigoCgbvp(), rolAsignado)
            );
        }
    }

    // Registra una nueva área
    @NonNull
    public AreaDTO crearArea(@NonNull AreaDTO dto) {

        // 1. Validar nombre único
        if (areaRepository.existsByNombreArea(dto.getNombreArea())) {
            throw new BusinessException("El área ya se encuentra registrada");
        }

        // 2. Validar fecha inicio >= 2026
        if (dto.getFechaInicio().getYear() < 2026) {
            throw new BusinessException(
                    "La fecha de inicio no puede ser anterior al año 2026");
        }

        // 3. Validar fecha fin > fecha inicio
        if (dto.getFechaFin() != null &&
            dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new BusinessException(
                    "La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // 4. Validar encargado1 único por área (regla existente)
        if (areaRepository.existsByEncargado1Id(dto.getEncargado1Id())) {
            throw new BusinessException(
                    "El bombero ya es encargado principal de otra área");
        }

        // ========== 5. NUEVO: Validar que el encargado principal no esté en otra área ==========
        validarEncargadoUnico(dto.getEncargado1Id(), null, "principal");

        // ========== 6. NUEVO: Validar que el encargado secundario no esté en otra área ==========
        if (dto.getEncargado2Id() != null) {
            validarEncargadoUnico(dto.getEncargado2Id(), null, "secundario");
        }

        Area area = dtoToEntity(dto);
        return entityToDto(areaRepository.save(area));
    }

    // Actualiza un área
    public AreaDTO actualizarArea(@NonNull Long id, AreaDTO dto) {

        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Área no encontrada"));

        // 1. Validar nombre único (excluyendo el área actual)
        if (!area.getNombreArea().equals(dto.getNombreArea())
                && areaRepository.existsByNombreArea(dto.getNombreArea())) {
            throw new BusinessException("Ya existe otra área con ese nombre");
        }

        // 2. Validar fecha inicio >= 2026
        if (dto.getFechaInicio().getYear() < 2026) {
            throw new BusinessException(
                    "La fecha de inicio no puede ser anterior al año 2026");
        }

        // 3. Validar encargado1 único por área (excluyendo el área actual)
        if (areaRepository.existsByEncargado1IdAndIdNot(
                dto.getEncargado1Id(), id)) {
            throw new BusinessException(
                    "El bombero ya es encargado principal de otra área");
        }

        // 4. Validar fecha fin > fecha inicio
        if (dto.getFechaFin() != null &&
            dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new BusinessException(
                    "La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // ========== 5. NUEVO: Validar que el encargado principal no esté en otra área ==========
        validarEncargadoUnico(dto.getEncargado1Id(), id, "principal");

        // ========== 6. NUEVO: Validar que el encargado secundario no esté en otra área ==========
        if (dto.getEncargado2Id() != null) {
            validarEncargadoUnico(dto.getEncargado2Id(), id, "secundario");
        }

        actualizarDatos(area, dto);
        return entityToDto(areaRepository.save(area));
    }

    // Elimina un área sin equipos asociados
    public void eliminar(@NonNull Long id) {

        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Área no encontrada"));

        if (!area.getEquipos().isEmpty()) {
            throw new BusinessException("No se puede eliminar un área con equipos asociados");
        }

        areaRepository.delete(area);
    }

    // Convierte DTO a Entity
    @NonNull
    private Area dtoToEntity(AreaDTO dto) {
        Area area = new Area();
        actualizarDatos(area, dto);
        return area;
    }

    // Actualiza los datos del área
    private void actualizarDatos(Area area, AreaDTO dto) {

        Bombero encargado1 = obtenerBomberoActivo(
            java.util.Objects.requireNonNull(dto.getEncargado1Id(), "El encargado 1 es requerido")
        );

        Bombero encargado2 = null;

        if (dto.getEncargado2Id() != null) {
            encargado2 = obtenerBomberoActivo(
                java.util.Objects.requireNonNull(dto.getEncargado2Id())
            );

            if (Objects.equals(encargado1.getId(), encargado2.getId())) {
                throw new BusinessException("Los encargados no pueden ser el mismo bombero");
            }
        }

        area.setNombreArea(dto.getNombreArea());
        area.setEncargado1(encargado1);
        area.setEncargado2(encargado2);
        area.setFechaInicio(dto.getFechaInicio());
        area.setFechaFin(dto.getFechaFin());
        area.setObservaciones(dto.getObservaciones());
        area.setActivo(dto.getActivo());
    }

    // Convierte Entity a DTO
    @NonNull
    private AreaDTO entityToDto(Area area) {
        return new AreaDTO(
                area.getId(),
                area.getNombreArea(),
                area.getEncargado1().getId(),
                area.getEncargado2() != null ? area.getEncargado2().getId() : null,
                area.getFechaInicio(),
                area.getFechaFin(),
                area.getObservaciones(),
                area.getActivo()
        );
    }

    // Obtiene un bombero activo
    private Bombero obtenerBomberoActivo(@NonNull Long id) {
        return bomberoRepository.findById(id)
                .filter(b -> b.getEstado() == EstadoBombero.ACTIVO)
                .orElseThrow(() ->
                        new BusinessException("El bombero seleccionado no existe o no se encuentra ACTIVO"));
    }
}