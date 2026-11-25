package com.fireequipmanager.backend.service;

import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fireequipmanager.backend.model.Area;
import com.fireequipmanager.backend.dto.EppDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Epp;
import com.fireequipmanager.backend.repository.AreaRepository;
import com.fireequipmanager.backend.repository.EppRepository;
import com.fireequipmanager.backend.service.rules.EstadoInventarioRule;
import com.fireequipmanager.backend.service.rules.VidaUtilRule;
import org.springframework.lang.NonNull;
import com.fireequipmanager.backend.repository.UbicacionRepository;
import com.fireequipmanager.backend.model.Ubicacion;

@Service
@Transactional
public class EppService {
    private final EppRepository eppRepository;
    private final AreaRepository areaRepository;
    private final UbicacionRepository ubicacionRepository;
    public EppService(
            EppRepository eppRepository,
            AreaRepository areaRepository,
            UbicacionRepository ubicacionRepository) 
        {
        this.eppRepository = eppRepository;
        this.areaRepository = areaRepository;
        this.ubicacionRepository = ubicacionRepository;
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
            dto.getMotivoBaja(),"EPP");
            Area area = obtenerArea(dto.getAreaId());
            Ubicacion ubicacion = obtenerUbicacion(dto.getUbicacionId());

            Epp epp = dtoToEntity(dto);

            epp.setArea(area);
            epp.setUbicacion(ubicacion);
            epp.setAsignado(false);
    return entityToDto(
            eppRepository.save(epp));

}

// Actualiza un EPP
    public EppDTO actualizarEpp(@NonNull Long id, @NonNull EppDTO dto) {

        Epp epp = obtenerEpp(id);
        Boolean asignadoActual = epp.getAsignado();
        validarDuplicados(dto, id);
        EstadoInventarioRule.validarEstado(
                dto.getEstado(),
                dto.getFechaBaja(),
                dto.getMotivoBaja(),
                "EPP"
        );

        Area area = obtenerArea(dto.getAreaId());
        Ubicacion ubicacion = obtenerUbicacion(dto.getUbicacionId());

        actualizarDatos(epp, dto);
        epp.setArea(area);
        epp.setUbicacion(ubicacion);

        if (dto.getAsignado() == null) {
            epp.setAsignado(asignadoActual);
        }

        return entityToDto(eppRepository.save(epp));
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

private Area obtenerArea(Long areaId) {
    if (areaId == null) {
        throw new BusinessException("El área es obligatoria");
    }
    return areaRepository.findById(areaId)
            .orElseThrow(() -> new BusinessException("Área no encontrada con id: " + areaId));
}

private Ubicacion obtenerUbicacion(Long ubicacionId) {
    if (ubicacionId == null) {
        throw new BusinessException("La ubicación es obligatoria");
    }
    return ubicacionRepository.findById(ubicacionId)
            .orElseThrow(() -> new BusinessException(
                    "Ubicación no encontrada con id: " + ubicacionId));
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
            epp.getId(),                                                      // 1  id
            epp.getCodigoCgbvp(),                                             // 2  codigoCgbvp
            epp.getNumeroSerie(),                                             // 3  numeroSerie
            epp.getTipoEpp(),                                                 // 4  tipoEpp
            epp.getNombreEpp(),                                               // 5  nombreEpp
            epp.getCompania(),                                                // 6  compania
            epp.getEstado(),                                                  // 7  estado
            epp.getFechaIncorporacion(),                                      // 8  fechaIncorporacion
            epp.getVidaUtilAnios(),                                           // 9  vidaUtilAnios
            epp.getFechaBaja(),                                               // 10 fechaBaja
            epp.getMotivoBaja(),                                              // 11 motivoBaja
            epp.getTalla(),                                                   // 12 talla
            epp.getMaterial(),                                                // 13 material
            epp.getMarca(),                                                   // 14 marca
            epp.getColor(),                                                   // 15 color
            epp.getObservaciones(),                                           // 16 observaciones
            epp.getArea() != null ? epp.getArea().getId() : null,             // 17 areaId
            epp.getUbicacion() != null ? epp.getUbicacion().getId() : null,   // 18 ubicacionId
            epp.getUbicacion() != null ? epp.getUbicacion().getCodigo() : null, // 19 ubicacionCodigo
            epp.getUbicacion() != null ? epp.getUbicacion().getNombre() : null, // 20 ubicacionNombre
            epp.getUbicacion() != null ? epp.getUbicacion().getTipo().name() : null, // 21 ubicacionTipo
            epp.getUbicacion() != null
                    ? epp.getUbicacion().getCodigo() + " - " + epp.getUbicacion().getNombre()
                    : null,                                                   // 22 ubicacionDisplay
            epp.getArea() != null ? epp.getArea().getNombreArea() : null,     // 23 areaNombre
            epp.getAsignado(),                                                // 24 asignado
            null,  // fechaVencimiento                                        // 25
            null,  // diasRestantes                                           // 26
            null,  // proximoAVencer                                          // 27
            null,  // vidaUtilVencida                                         // 28
            null,  // alertaStock                                             // 29
            null,  // sinStock                                                // 30
            null   // porcentajeStock                                         // 31
    );

    completarIndicadores(dto, epp);

    return dto;
}
}