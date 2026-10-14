package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.dto.CasilleroDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Casillero;
import com.fireequipmanager.backend.model.enumsUbicacion.PisoUbicacion;
import com.fireequipmanager.backend.repository.CasilleroRepository;

import jakarta.transaction.Transactional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CasilleroService {

    private final CasilleroRepository casilleroRepository;

    public CasilleroService(CasilleroRepository casilleroRepository) {
        this.casilleroRepository = casilleroRepository;
    }

    // Lista todos los casilleros
    public List<CasilleroDTO> listarTodos() {

        return casilleroRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    // Lista únicamente los casilleros disponibles
    public List<CasilleroDTO> listarDisponibles() {

        return casilleroRepository.findByAsignadoFalse()
                .stream()
                .map(this::entityToDto)
                .toList();
    }
    // : Lista únicamente los casilleros habilitados administrativamente (activo = true)
    public List<CasilleroDTO> listarActivos() {
        return casilleroRepository.findByActivoTrue()
                .stream()
                .map(this::entityToDto)
                .toList();
    }
    // Busca un casillero por ID
    public CasilleroDTO buscarPorId(@NonNull Long id) {

        return entityToDto(obtenerCasillero(id));
    }
    

    // Registra un nuevo casillero
    public CasilleroDTO crear(@NonNull CasilleroDTO dto) {

        validarDatos(dto);
        validarDuplicado(dto, null);
        Casillero casillero = dtoToEntity(dto);
        casillero.setAsignado(false);
        return entityToDto(
                casilleroRepository.save(casillero)
        );
    }
    // 🚀 NUEVO MÉTODO: Lista los casilleros filtrados por piso (Enum)
    public List<CasilleroDTO> listarPorPiso(String pisoTexto) {
        if (pisoTexto == null || pisoTexto.isBlank()) {
            throw new BusinessException("El parámetro de piso no puede estar vacío");
        }

        try {
            // 1. Convertimos el String plano de la URL al Enum de tu arquitectura
            PisoUbicacion pisoEnum = PisoUbicacion.valueOf(pisoTexto.toUpperCase().trim());
            
            // 2. Consultamos al repositorio, procesamos en stream y mapeamos al DTO
            return casilleroRepository.findByPisoUbicacion(pisoEnum)
                    .stream()
                    .map(this::entityToDto)
                    .toList();
                    
        } catch (IllegalArgumentException e) {
            // Protección empresarial si mandan datos inválidos en la ruta (ej: /piso/subterraneo)
            throw new BusinessException("El piso especificado no es válido en el sistema de casilleros.");
        }
    }

    // Actualiza un casillero
    public CasilleroDTO actualizar(
            @NonNull Long id,
            @NonNull CasilleroDTO dto) {

        Casillero casillero = obtenerCasillero(id);

        validarDatos(dto);
        validarDuplicado(dto, id);

        Boolean asignadoActual = casillero.getAsignado();

        actualizarDatos(casillero, dto);

        // No permite perder el estado de asignación accidentalmente
        if (dto.getAsignado() == null) {
            casillero.setAsignado(asignadoActual);
        }

        return entityToDto(
                casilleroRepository.save(casillero)
        );
    }

    // Elimina un casillero
    public void eliminar(@NonNull Long id) {
        Casillero casillero = obtenerCasillero(id);
        validarEliminacion(casillero);
         casilleroRepository.delete(Objects.requireNonNull(casillero, "El casillero a eliminar no puede ser nulo"));
    }

    // Valida los datos básicos
    private void validarDatos(CasilleroDTO dto) {

        if (dto == null) {
            throw new BusinessException(
                    "Los datos del casillero no pueden ser nulos"
            );
        }

        if (dto.getNumIdentificadorCasillero() == null) {
            throw new BusinessException(
                    "El número del casillero es obligatorio"
            );
        }

        if (dto.getNumIdentificadorCasillero() <= 0) {
            throw new BusinessException(
                    "El número del casillero debe ser mayor a cero"
            );
        }

        if (dto.getMaterialCasillero() == null) {
            throw new BusinessException(
                    "Debe seleccionar el material del casillero"
            );
        }

        if (dto.getPisoUbicacion() == null) {
            throw new BusinessException(
                    "Debe seleccionar el piso del casillero"
            );
        }
    }

    // Valida que no exista otro casillero con el mismo número
    private void validarDuplicado(
            CasilleroDTO dto,
            Long id) {

        if (id == null) {

            if (casilleroRepository
                    .existsByNumIdentificadorCasillero(
                            dto.getNumIdentificadorCasillero())) {
                throw new BusinessException(
                        "Ya existe un casillero con ese número"
                );
            }

            return;
        }

        if (casilleroRepository
                .existsByNumIdentificadorCasilleroAndIdNot(
                        dto.getNumIdentificadorCasillero(),
                        id)) {

            throw new BusinessException(
                    "Ya existe otro casillero con ese número"
            );
        }
    }

    // Valida si el casillero puede eliminarse
    private void validarEliminacion(Casillero casillero) {

        if (Boolean.TRUE.equals(casillero.getAsignado())) {

            throw new BusinessException(
                    "No se puede eliminar un casillero asignado a un bombero"
            );
        }

        // Aquí posteriormente validaremos ubicaciones asociadas.
    }

    // Obtiene un casillero por ID
    private Casillero obtenerCasillero(@NonNull Long id) {

        return casilleroRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                "Casillero no encontrado"
                        ));
    }

    // Convierte DTO a entidad
    private Casillero dtoToEntity(CasilleroDTO dto) {
        Casillero casillero = new Casillero();
        actualizarDatos(casillero, dto);
        return casillero;
    }

        // 🚀 NUEVO MÉTODO: Habilita administrativamente un casillero
    public CasilleroDTO activar(@NonNull Long id) {
        // 1. Buscamos el casillero usando tu método defensivo existente
        Casillero casillero = obtenerCasillero(id);

        // 2. Modificamos el estado booleano
        casillero.setActivo(true);

        // 3. Guardamos los cambios y retornamos el DTO transformado
        return entityToDto(casilleroRepository.save(casillero));
    }

    // 🚀 NUEVO MÉTODO: Deshabilita administrativamente un casillero
    public CasilleroDTO desactivar(@NonNull Long id) {
        // 1. Buscamos el casillero
        Casillero casillero = obtenerCasillero(id);

        // 2. Modificamos el estado booleano
        casillero.setActivo(false);

        // 3. Guardamos los cambios y retornamos el DTO transformado
        return entityToDto(casilleroRepository.save(casillero));
    }


    // Actualiza los datos del casillero
    private void actualizarDatos(Casillero casillero, CasilleroDTO dto) {
        // 🚀 Agregado: Mapeo del código interno obligatorio
        casillero.setCodigo(dto.getCodigo()); 
        
        casillero.setNumIdentificadorCasillero(dto.getNumIdentificadorCasillero());
        casillero.setMaterialCasillero(dto.getMaterialCasillero());
        casillero.setPisoUbicacion(dto.getPisoUbicacion());
        casillero.setDescripcion(dto.getDescripcion());
        
        if (dto.getActivo() != null) {
            casillero.setActivo(dto.getActivo());
        }
        if (dto.getOcupado() != null) {
            casillero.setOcupado(dto.getOcupado());
        }
        if (dto.getAsignado() != null) {
            casillero.setAsignado(dto.getAsignado());
        }
    }
  
    // Convierte entidad a DTO (🚀 ¡Encaja con los 10 parámetros exactos!)
    private CasilleroDTO entityToDto(Casillero casillero) {
        if (casillero == null) {
            return null;
        }

        return new CasilleroDTO(
                casillero.getId(),
                casillero.getCodigo(),
                casillero.getNumIdentificadorCasillero(),
                casillero.getMaterialCasillero(),
                casillero.getPisoUbicacion(),
                casillero.getDescripcion(),
                casillero.getActivo(),
                casillero.getOcupado(),
                casillero.getAsignado(),
                // 🚀 Corregido: Convertimos el Enum a String usando .name() de manera segura contra nulos
                casillero.getUbicacion() != null && casillero.getUbicacion().getNombreUbicacion() != null ? 
                        casillero.getUbicacion().getNombreUbicacion().name() : null
        );
    }
}
