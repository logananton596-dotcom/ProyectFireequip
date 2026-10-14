package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.dto.OficinaDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Oficina;
import com.fireequipmanager.backend.model.enumsUbicacion.PisoUbicacion;
import com.fireequipmanager.backend.repository.OficinaRepository;

import jakarta.transaction.Transactional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class OficinaService {

    private final OficinaRepository oficinaRepository;

    public OficinaService(OficinaRepository oficinaRepository) {
        this.oficinaRepository = oficinaRepository;
    }

    // Lista todas las oficinas
    public List<OficinaDTO> listarTodos() {

        return oficinaRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Lista únicamente las oficinas activas
    public List<OficinaDTO> listarActivas() {

        return oficinaRepository.findByActivaTrue()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Busca una oficina por ID
    public OficinaDTO buscarPorId(@NonNull Long id) {

        return entityToDto(obtenerOficina(id));
    }

    // Registra una nueva oficina
    public OficinaDTO crear(@NonNull OficinaDTO dto) {

        validarDatos(dto);
        validarDuplicado(dto, null);

        Oficina oficina = dtoToEntity(dto);

        oficina.setActiva(true);

        return entityToDto(
                oficinaRepository.save(oficina)
        );
    }

    // Actualiza una oficina
    public OficinaDTO actualizar(
            @NonNull Long id,
            @NonNull OficinaDTO dto) {

        Oficina oficina = obtenerOficina(id);

        validarDatos(dto);
        validarDuplicado(dto, id);

        actualizarDatos(oficina, dto);

        return entityToDto(
                oficinaRepository.save(Objects.requireNonNull(oficina, "Error al guardar la oficina en el sistema"))
        );
    }

    // Activa una oficina
    public OficinaDTO activar(@NonNull Long id) {

        Oficina oficina = obtenerOficina(id);

        oficina.setActiva(true);

        return entityToDto(
                oficinaRepository.save(oficina)
        );
    }

    // Desactiva una oficina
    public OficinaDTO desactivar(@NonNull Long id) {

        Oficina oficina = obtenerOficina(id);

        oficina.setActiva(false);

        return entityToDto(
                oficinaRepository.save(oficina)
        );
    }

    // Elimina una oficina
    public void eliminar(@NonNull Long id) {

        Oficina oficina = obtenerOficina(id);

        validarEliminacion(oficina);

        oficinaRepository.delete(Objects.requireNonNull(oficina, "La oficina a eliminar no puede ser nula"));
    }

    // Valida los datos básicos
    private void validarDatos(OficinaDTO dto) {

        if (dto == null) {
            throw new BusinessException(
                    "Los datos de la oficina no pueden ser nulos"
            );
        }

        if (dto.getNombreOficina() == null) {
            throw new BusinessException(
                    "Debe seleccionar el nombre de la oficina"
            );
        }

        if (dto.getPisoUbicacion() == null) {
            throw new BusinessException(
                    "Debe seleccionar el piso de la oficina"
            );
        }
    }

    // Valida oficinas duplicadas
    private void validarDuplicado(
            OficinaDTO dto,
            Long id) {

        if (id == null) {

            if (oficinaRepository.existsByNombreOficinaAndUbicacionPiso(
                    dto.getNombreOficina(),
                    dto.getPisoUbicacion())) {

                throw new BusinessException(
                        "Ya existe una oficina con ese nombre en ese piso"
                );
            }

            return;
        }

        if (oficinaRepository
                .existsByNombreOficinaAndUbicacionPisoAndIdNot(
                        dto.getNombreOficina(),
                        dto.getPisoUbicacion(),
                        id)) {

            throw new BusinessException(
                    "Ya existe otra oficina con ese nombre en ese piso"
            );
        }
    }

    // Valida si una oficina puede eliminarse
    private void validarEliminacion(Oficina oficina) {

        if (Boolean.TRUE.equals(oficina.getActiva())) {

            throw new BusinessException(
                    "No se puede eliminar una oficina activa"
            );
        }

        // Aquí validaremos posteriormente sus ubicaciones asignadas.
    }

    // Obtiene una oficina por ID
    private Oficina obtenerOficina(@NonNull Long id) {

        return oficinaRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                "Oficina no encontrada"
                        ));
    }

        // 🚀 NUEVO MÉTODO: Lista oficinas filtradas por piso (Enum)
    public List<OficinaDTO> listarPorPiso(String pisoTexto) {
        if (pisoTexto == null || pisoTexto.isBlank()) {
            throw new BusinessException("El parámetro de piso no puede estar vacío");
        }

        try {
            // 1. Convertimos el String que viene del Controller al Enum real de tu arquitectura
            PisoUbicacion pisoEnum = PisoUbicacion.valueOf(pisoTexto.toUpperCase().trim());
            
            // 2. Buscamos en el repositorio, transmitimos al stream y mapeamos al DTO
            return oficinaRepository.findByUbicacionPiso(pisoEnum)
                    .stream()
                    .map(this::entityToDto)
                    .toList();
                    
        } catch (IllegalArgumentException e) {
            // Protección defensiva si mandan un texto inválido en la URL (ej: /piso/octavo)
            throw new BusinessException("El piso especificado no es válido en el sistema.");
        }
    }


    // Convierte DTO a entidad
    private Oficina dtoToEntity(OficinaDTO dto) {

        Oficina oficina = new Oficina();

        actualizarDatos(oficina, dto);

        return oficina;
    }

    // Actualiza los datos de la oficina
    private void actualizarDatos(
            Oficina oficina,
            OficinaDTO dto) {

        oficina.setNombreOficina(dto.getNombreOficina());
        oficina.setUbicacionPiso(dto.getPisoUbicacion());
        oficina.setObservacion(dto.getObservacion());

        if (dto.getActiva() != null) {
            oficina.setActiva(dto.getActiva());
        }
    }

    // Convierte entidad a DTO
// Convierte entidad a DTO
private OficinaDTO entityToDto(Oficina oficina) {
    if (oficina == null) {
        return null;
    }

    return new OficinaDTO(
            oficina.getId(),                     
            oficina.getNombreOficina(),           
            oficina.getUbicacionPiso(),             
            null,          
            oficina.getObservacion(),               
            oficina.getActiva(),                   
            oficina.getReferencia()            
    );
    }
}