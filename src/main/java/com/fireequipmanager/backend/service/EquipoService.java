package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.model.EquipoHistorial;
import com.fireequipmanager.backend.model.EstadoEquipo;
import com.fireequipmanager.backend.repository.EquipoHistorialRepository;
import com.fireequipmanager.backend.repository.EquipoRepository;
import com.fireequipmanager.backend.repository.EstadoEquipoRepository;

import com.fireequipmanager.backend.exception.*;
import jakarta.transaction.Transactional;


import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final EquipoHistorialRepository historialRepository; 
    private final EstadoEquipoRepository estadoRepository; 

    public EquipoService(EquipoRepository equipoRepository, 
                         EquipoHistorialRepository historialRepository,
                         EstadoEquipoRepository estadoRepository) {
        this.equipoRepository = equipoRepository;
        this.historialRepository = historialRepository;
        this.estadoRepository = estadoRepository;
    }

   // --- MÉTODOS DE BÚSQUEDA ---

    public List<Equipo> listarTodos() {
        return equipoRepository.findAll();
    }

    public Equipo buscarPorId(Long id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Equipo no encontrado con ID: " + id));
    }

    public List<Equipo> equiposPorVencer() {
        return equipoRepository.findAll().stream()
                .filter(e -> {
                    if (e.getFechaCompra() == null || e.getVidaUtilAnios() == null) return false;
                    LocalDate fechaVencimiento = e.getFechaCompra().plusYears(e.getVidaUtilAnios());
                    return fechaVencimiento.minusDays(30).isBefore(LocalDate.now());
                })
                .toList();
    }
    
    // --- OPERACIONES PRINCIPALES ---

    public Equipo crearEquipo(Equipo equipo) {
        if (equipo.getEstadoEquipo() == null || equipo.getTipoEquipo() == null) {
            throw new BusinessException("El estado y el tipo de equipo son obligatorios");
        }
        if (equipoRepository.existsByNumeroSerie(equipo.getNumeroSerie())) {
            throw new BusinessException("El número de serie ya existe en el sistema");
        }
        return equipoRepository.save(equipo);
    }

    public Equipo actualizarEquipo(Long id, Equipo equipoNuevo, String username) {
        Equipo equipoBD = buscarPorId(id);

        // Validar número de serie duplicado (excluyendo al equipo actual)
        if (equipoRepository.existsByNumeroSerieAndIdNot(equipoNuevo.getNumeroSerie(), id)) {
            throw new BusinessException("El nuevo número de serie ya está registrado en otro equipo");
        }

        // Validaciones de Negocio
        validarTransicionEstado(equipoBD.getEstadoEquipo(), equipoNuevo.getEstadoEquipo());
        validarVidaUtil(equipoNuevo);

        // Historial (Solo si el nombre cambió)
        if (!equipoBD.getNombre().equals(equipoNuevo.getNombre())) {
            guardarHistorial(equipoBD, "nombre", equipoBD.getNombre(), equipoNuevo.getNombre(), username);
        }

        // Mapeo de campos
        equipoBD.setNombre(equipoNuevo.getNombre());
        equipoBD.setNumeroSerie(equipoNuevo.getNumeroSerie());
        equipoBD.setMarca(equipoNuevo.getMarca());
        equipoBD.setModelo(equipoNuevo.getModelo());
        equipoBD.setEstadoEquipo(equipoNuevo.getEstadoEquipo());
        equipoBD.setTipoEquipo(equipoNuevo.getTipoEquipo());
        equipoBD.setFechaCompra(equipoNuevo.getFechaCompra());
        equipoBD.setVidaUtilAnios(equipoNuevo.getVidaUtilAnios());
        equipoBD.setCodigoInterno(equipoNuevo.getCodigoInterno());

        return equipoRepository.save(equipoBD);
    }

    public void darDeBaja(Long id, String motivo, String autorizado) {
        Equipo equipo = buscarPorId(id);

        if (motivo == null || autorizado == null || motivo.isBlank()) {
            throw new BusinessException("El motivo y la autorización son obligatorios para dar de baja");
        }

        EstadoEquipo estadoBaja = estadoRepository.findByNombre("DADO_BAJA")
                .orElseThrow(() -> new BusinessException("Estado 'DADO_BAJA' no encontrado en el sistema"));

        equipo.setMotivoBaja(motivo);
        equipo.setAutorizadoPor(autorizado);
        equipo.setFechaBaja(LocalDate.now());
        equipo.setEstadoEquipo(estadoBaja);

        equipoRepository.save(equipo);
    }

    public void eliminar(Long id) {
        if (!equipoRepository.existsById(id)) {
            throw new BusinessException("No se puede eliminar: El equipo no existe");
        }
        equipoRepository.deleteById(id);
    }

    // --- REPORTES ---

    public Map<String, Long> reportePorEstado() {
        Map<String, Long> reporte = new HashMap<>();
        for (Object[] obj : equipoRepository.countByEstado()) {
            reporte.put((String) obj[0], (Long) obj[1]);
        }
        return reporte; 
    }

    // --- MÉTODOS PRIVADOS DE APOYO ---

    private void guardarHistorial(Equipo equipo, String campo, String oldVal, String newVal, String user) {
        EquipoHistorial h = new EquipoHistorial();
        h.setEquipo(equipo);
        h.setCampoModificado(campo);
        h.setValorAnterior(oldVal);
        h.setValorNuevo(newVal);
        h.setFechaCambio(LocalDateTime.now());
        h.setUsuario(user != null ? user : "SISTEMA");
        historialRepository.save(h);
    }

    private void validarTransicionEstado(EstadoEquipo actual, EstadoEquipo nuevo) {
        if (actual == null || nuevo == null) return;
        if (actual.getNombre().contains("BAJA") && nuevo.getNombre().equals("OPERATIVO")) {
            throw new BusinessException("Restricción: No se puede reactivar un equipo dado de baja directamente.");
        }
    }

    private void validarVidaUtil(Equipo equipo) {
        if (equipo.getFechaCompra() == null || equipo.getVidaUtilAnios() == null) return;
        
        LocalDate fechaVencimiento = equipo.getFechaCompra().plusYears(equipo.getVidaUtilAnios());
        if (LocalDate.now().isAfter(fechaVencimiento) && equipo.getEstadoEquipo().getNombre().equals("OPERATIVO")) {
            throw new BusinessException("El equipo ha superado su vida útil; no puede estar en estado OPERATIVO.");
        }
    }
}

