package com.fireequipmanager.backend.service;

import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fireequipmanager.backend.dto.EppDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Area;
import com.fireequipmanager.backend.model.Epp;
import com.fireequipmanager.backend.model.enumsArea.NombreArea; 
import com.fireequipmanager.backend.repository.AreaRepository;
import com.fireequipmanager.backend.repository.EppRepository;
import com.fireequipmanager.backend.service.rules.EstadoInventarioRule;
import com.fireequipmanager.backend.service.rules.VidaUtilRule;
import org.springframework.lang.NonNull;

@Service
@Transactional
public class EppService {
    private final EppRepository eppRepository;
    private final AreaRepository areaRepository;
    public EppService(
            EppRepository eppRepository,
            AreaRepository areaRepository
            //stadoInventarioRule estadoInventarioRule,
            //VidaUtilRule vidaUtilRule
            ) 
            {

        this.eppRepository = eppRepository;
        this.areaRepository = areaRepository;
    }
// Lista todos los EPP
public List<EppDTO> listarTodos() {

    return eppRepository.findAll()
            .stream()
            .map(this::entityToDto)
            .toList();

}

// Busca un EPP
public EppDTO buscarPorId(@NonNull Long id) {

    return entityToDto(obtenerEpp(id));

}

// Registra un EPP
public EppDTO crearEpp(@NonNull EppDTO dto) {

    validarDuplicados(dto, null);

    EstadoInventarioRule.validarEstado(
            dto.getEstado(),
            dto.getFechaBaja(),
            dto.getMotivoBaja(),
        "EPP");
    Area area = obtenerArea(
            Objects.requireNonNull(dto.getNombreArea()));
    Epp epp = dtoToEntity(dto);

    epp.setArea(area);
    epp.setAsignado(false);

    return entityToDto(
            eppRepository.save(epp));

}

// Actualiza un EPP
public EppDTO actualizarEpp(
        @NonNull Long id,
        @NonNull EppDTO dto) {

    Epp epp = obtenerEpp(id);

    Boolean asignadoActual = epp.getAsignado();

    validarDuplicados(dto, id);

    EstadoInventarioRule.validarEstado(
            dto.getEstado(),
            dto.getFechaBaja(),
            dto.getMotivoBaja(),
            "EPP");

    actualizarDatos(epp, dto);

    epp.setArea(
            obtenerArea(
                    Objects.requireNonNull(dto.getNombreArea())));

    if (dto.getAsignado() == null) {
        epp.setAsignado(asignadoActual);
    }

    return entityToDto(
            eppRepository.save(epp));

}

// Eliminación lógica futura
public void eliminar(@NonNull Long id) {
    Epp epp = obtenerEpp(id);
    validarEliminacion(epp);
    eppRepository.delete(Objects.requireNonNull(epp, "El EPP a eliminar no puede ser nulo"));
}
// Valida duplicados
private void validarDuplicados(
        EppDTO dto,
        Long id) {

    if (id == null) {

        if (eppRepository.existsByCodigoCgbvp(dto.getCodigoCgbvp())) {
            throw new BusinessException(
                    "El código CGBVP ya existe");
        }

        if (eppRepository.existsByNumeroSerie(dto.getNumeroSerie())) {
            throw new BusinessException(
                    "El número de serie ya existe");
        }

        return;
    }

    if (eppRepository.existsByCodigoCgbvpAndIdNot(
            dto.getCodigoCgbvp(),
            id)) {

        throw new BusinessException(
                "El código CGBVP pertenece a otro EPP");
    }

    if (eppRepository.existsByNumeroSerieAndIdNot(
            dto.getNumeroSerie(),
            id)) {

        throw new BusinessException(
                "El número de serie pertenece a otro EPP");
    }

}

// Valida eliminación
private void validarEliminacion(Epp epp) {

    // Validar asignaciones
    // Validar mantenimientos
    // Validar movimientos

    if (Boolean.TRUE.equals(epp.getAsignado())) {

        throw new BusinessException(
                "No puede eliminar un EPP asignado.");
    }

}
// Obtiene un EPP
private Epp obtenerEpp(@NonNull Long id) {

    return eppRepository.findById(id)
            .orElseThrow(() ->
                    new BusinessException(
                            "EPP no encontrado"));

}

// Obtiene un área
private Area obtenerArea(
        @NonNull NombreArea nombreArea) {

    return areaRepository.findByNombreArea(nombreArea)
            .orElseThrow(() ->
                    new BusinessException(
                            "Área no encontrada"));

}

// Calcula indicadores automáticos
private void completarIndicadores(
        EppDTO dto,
        Epp epp) {
    VidaUtilRule.completarIndicadoresDeVidaUtil(dto, epp);
}
// Convierte DTO a Entity
private Epp dtoToEntity(EppDTO dto) {

    Epp epp = new Epp();

    actualizarDatos(epp, dto);

    return epp;

}
// Actualiza datos
private void actualizarDatos(
        Epp epp,
        EppDTO dto) {

    epp.setCodigoCgbvp(dto.getCodigoCgbvp());
    epp.setNumeroSerie(dto.getNumeroSerie());
    epp.setNombreEpp(dto.getNombreEpp());
    epp.setTipoEpp(dto.getTipoEpp());
    epp.setCompania(dto.getCompania());
    epp.setEstado(dto.getEstado());
    epp.setFechaIncorporacion(dto.getFechaIncorporacion());
    epp.setVidaUtilAnios(dto.getVidaUtilAnios());
    epp.setFechaBaja(dto.getFechaBaja());
    epp.setMotivoBaja(dto.getMotivoBaja());
    epp.setTalla(dto.getTalla());
    epp.setMaterial(dto.getMaterial());
    epp.setMarca(dto.getMarca());
    epp.setColor(dto.getColor());
    epp.setObservaciones(dto.getObservaciones());
    epp.setAsignado(dto.getAsignado());
}
// Convierte Entity a DTO
private EppDTO entityToDto(Epp epp) {

    EppDTO dto = new EppDTO(

            epp.getId(),
            epp.getCodigoCgbvp(),
            epp.getNumeroSerie(),
            epp.getTipoEpp(),
            epp.getNombreEpp(),
            epp.getCompania(),
            epp.getEstado(),
            epp.getFechaIncorporacion(),
            epp.getVidaUtilAnios(),
            epp.getFechaBaja(),
            epp.getMotivoBaja(),
            epp.getTalla(),
            epp.getMaterial(),
            epp.getMarca(),
            epp.getColor(),
            epp.getObservaciones(),
            epp.getArea().getNombreArea(),
            epp.getAsignado(),
            // Indicadores (se calculan después)
            null,
            null,
            null,
            null,            null,
            null,
            null

    );

    completarIndicadores(dto, epp);

    return dto;
}
}