package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.dto.EquipoDTO;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Area;
import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.model.enumsArea.NombreArea;
import com.fireequipmanager.backend.model.enumsEquipo.TipoInventario;
import com.fireequipmanager.backend.repository.AreaRepository;
import com.fireequipmanager.backend.repository.EquipoRepository;
import com.fireequipmanager.backend.service.rules.EstadoInventarioRule;
import com.fireequipmanager.backend.service.rules.InventarioRule;
import com.fireequipmanager.backend.service.rules.StockRule;
import com.fireequipmanager.backend.service.rules.VidaUtilRule;

import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.List;

@Service
@Transactional
public class EquipoService {

private final EquipoRepository equipoRepository;
private final AreaRepository areaRepository;
public EquipoService(
        EquipoRepository equipoRepository,
        AreaRepository areaRepository) 
        {
    this.equipoRepository = equipoRepository;
    this.areaRepository = areaRepository;
}

    // Lista todos los equipos
public List<EquipoDTO> listarTodos() {
    return equipoRepository.findAll()
            .stream()
            .map(this::entityToDto)
            .toList();
}
// Busca un equipo por ID
public EquipoDTO buscarPorId(@NonNull Long id) {
    return entityToDto(obtenerEquipo(id));
}
    // Registra un nuevo equipo

public EquipoDTO crearEquipo(@NonNull EquipoDTO dto) {

    InventarioRule.validarInventario(
            dto.getTipoInventario(),
            dto.getCodigoCgbvp(),
            dto.getNumeroSerie(),
            dto.getStock()
    );
   EstadoInventarioRule.validarEstado(
        dto.getEstado(),
        dto.getFechaBaja(),
        dto.getMotivoBaja(),
        "BAJA"
);
    validarDuplicados(dto,null);
    Area area = obtenerArea(Objects.requireNonNull(dto.getNombreArea(), "El área no puede ser nula"));
    Equipo equipo = dtoToEntity(dto);
    equipo.setArea(area);
    equipo.setAsignado(false);
    return entityToDto(
            equipoRepository.save(equipo)
    );
}

    // Actualiza un equipo
public EquipoDTO actualizarEquipo(
        @NonNull Long id,
        @NonNull EquipoDTO dto) {

    Equipo equipo = obtenerEquipo(id);
    validarDuplicados(dto, id);
    InventarioRule.validarInventario(
            dto.getTipoInventario(),
            dto.getCodigoCgbvp(),
            dto.getNumeroSerie(),
            dto.getStock()
    );
    EstadoInventarioRule.validarEstado(
        dto.getEstado(),
        dto.getFechaBaja(),
        dto.getMotivoBaja(),
        "BAJA"
);
    actualizarDatos(equipo, dto);
equipo.setArea(obtenerArea(Objects.requireNonNull(dto.getNombreArea(), "El área es obligatoria")));
    return entityToDto(
            equipoRepository.save(equipo)
    );
}

    // Elimina un equipo
public void eliminar(@NonNull Long id) {
    
    Equipo equipo = obtenerEquipo(id);
    validarEliminacion(equipo);
    equipoRepository.delete(Objects.requireNonNull(equipo, "El equipo a eliminar no puede ser nulo"));
}

    // VALIDACIONES
    // Valida duplicados solo cuando existe el dato
private void validarDuplicados(
        EquipoDTO dto,
        Long id) {
    if (dto.getTipoInventario() != TipoInventario.INDIVIDUAL) {
        return;
    }
    if (id == null) {
        if (equipoRepository.existsByCodigoCgbvp(dto.getCodigoCgbvp())) {
            throw new BusinessException(
                    "El código CGBVP ya existe");
        }
        if (equipoRepository.existsByNumeroSerie(dto.getNumeroSerie())) {
            throw new BusinessException(
                    "El número de serie ya existe");
        }
        return;
    }
    if (equipoRepository.existsByCodigoCgbvpAndIdNot(
            dto.getCodigoCgbvp(), id)) {
        throw new BusinessException(
                "El código CGBVP pertenece a otro equipo");
    }
    if (equipoRepository.existsByNumeroSerieAndIdNot(
            dto.getNumeroSerie(), id)) {
        throw new BusinessException(
                "El número de serie pertenece a otro equipo");
    }
}

private void completarIndicadores(
        EquipoDTO dto,
        Equipo equipo) {
    VidaUtilRule.completarIndicadoresDeVidaUtil(dto, equipo);
    StockRule.completarIndicadoresDeStock(dto, equipo);
}
    // Validaciones futuras
private void validarEliminacion(Equipo equipo) {
        // Validar asignaciones
        // Validar mantenimientos
        // Validar movimientos
        if(Boolean.TRUE.equals(equipo.getAsignado())){

        throw new BusinessException(
            "No puede eliminar un equipo asignado.");
    }
    }

    // OBTENER ENTIDADES

    private Equipo obtenerEquipo(@NonNull Long id) {
        return equipoRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException("Equipo no encontrado"));
    }

    private Area obtenerArea(@NonNull NombreArea nombreArea) {
        return areaRepository.findByNombreArea(nombreArea)
                .orElseThrow(() ->
                        new BusinessException("Área no encontrada"));
    }

    // MAPPERS
    private Equipo dtoToEntity(EquipoDTO dto) {
        Equipo equipo = new Equipo();
        actualizarDatos(equipo, dto);
        return equipo;
    }

    private void actualizarDatos(Equipo equipo, EquipoDTO dto) {

        equipo.setCodigoCgbvp(dto.getCodigoCgbvp());
        equipo.setNumeroSerie(dto.getNumeroSerie());
        equipo.setNombreEquipo(dto.getNombreEquipo());
        equipo.setTipoEquipo(dto.getTipoEquipo());
        equipo.setTipoInventario(dto.getTipoInventario());
        equipo.setCompania(dto.getCompania());
        equipo.setEstado(dto.getEstado());
        equipo.setFechaIncorporacion(dto.getFechaIncorporacion());
        equipo.setVidaUtilAnios(dto.getVidaUtilAnios());
        equipo.setFechaBaja(dto.getFechaBaja());
        equipo.setMotivoBaja(dto.getMotivoBaja());
        equipo.setStock(dto.getStock());
        equipo.setStockMinimo(dto.getStockMinimo());
        equipo.setEspecificacion(dto.getEspecificacion());
        equipo.setDescripcion(dto.getDescripcion()); 
        equipo.setMaterial(dto.getMaterial());
        equipo.setMarca(dto.getMarca());
        equipo.setModelo(dto.getModelo());
        equipo.setColor(dto.getColor());
        equipo.setObservaciones(dto.getObservaciones());
        equipo.setAsignado(dto.getAsignado());
    }

    private EquipoDTO entityToDto(Equipo equipo) {

        EquipoDTO dto = new EquipoDTO(

                equipo.getId(),
                equipo.getCodigoCgbvp(),
                equipo.getNumeroSerie(),
                equipo.getNombreEquipo(),
                equipo.getTipoEquipo(),
                equipo.getTipoInventario(),
                equipo.getCompania(),
                equipo.getEstado(),
                equipo.getFechaIncorporacion(),
                equipo.getVidaUtilAnios(),
                equipo.getFechaBaja(),
                equipo.getMotivoBaja(),
                equipo.getStock(),
                equipo.getStockMinimo(),
                equipo.getEspecificacion(),
                equipo.getDescripcion(),
                equipo.getMaterial(),
                equipo.getMarca(),
                equipo.getModelo(),
                equipo.getColor(),
                equipo.getObservaciones(),
                equipo.getArea().getNombreArea(),
                equipo.getAsignado(),
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        completarIndicadores(dto, equipo);
        return dto;
    }
}