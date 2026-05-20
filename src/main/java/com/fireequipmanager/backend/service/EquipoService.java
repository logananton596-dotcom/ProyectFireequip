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
    private final EstadoEquipoRepository estadoEquipoRepository;

    public EquipoService(EquipoRepository equipoRepository, 
                         EquipoHistorialRepository historialRepository,
                         EstadoEquipoRepository estadoEquipoRepository) {
        this.equipoRepository = equipoRepository;
        this.historialRepository = historialRepository;
        this.estadoEquipoRepository = estadoEquipoRepository;
    }
    public Equipo guardar(Equipo equipo) {
        return equipoRepository.save(equipo);
    }

    public List<Equipo> listarTodos() {
        return equipoRepository.findAll();
    }

    public Equipo buscarPorId(Long id) {
        return equipoRepository.findById(id).orElse(null);
    }

    public Equipo actualizar(Long id, Equipo equipoActualizado) {
        return equipoRepository.findById(id).map(equipo -> {
            equipo.setCodigoInterno(equipoActualizado.getCodigoInterno());
            equipo.setNumeroSerie(equipoActualizado.getNumeroSerie());
            equipo.setNombre(equipoActualizado.getNombre());
            equipo.setMarca(equipoActualizado.getMarca());
            equipo.setModelo(equipoActualizado.getModelo());
            equipo.setFechaCompra(equipoActualizado.getFechaCompra());
            equipo.setVidaUtilAnios(equipoActualizado.getVidaUtilAnios());
            equipo.setUbicacionActual(equipoActualizado.getUbicacionActual());
            equipo.setTipoEquipo(equipoActualizado.getTipoEquipo());
            equipo.setEstadoEquipo(equipoActualizado.getEstadoEquipo());
            return equipoRepository.save(equipo);
        }).orElse(null);
    }

    public void eliminar(Long id) {
        equipoRepository.deleteById(id);
    }
    
    public List<Equipo> equiposPorVencer() {
        return equipoRepository.findAll().stream()
                .filter(e -> {
                    if (e.getFechaCompra() == null || e.getVidaUtilAnios() == null) return false;

                    LocalDate fechaVencimiento = e.getFechaCompra()
                            .plusMonths(e.getVidaUtilAnios());

                    return fechaVencimiento.minusDays(30).isBefore(LocalDate.now());
                })
                .toList();
    }

    public Equipo crearEquipo(Equipo equipo) {

        // RN-02: estado obligatorio
        if (equipo.getEstadoEquipo() == null) {
            //throw new RuntimeException("El estado es obligatorio");
            throw new IllegalArgumentException("mensaje");
        }

        // RN-02: tipo obligatorio
        if (equipo.getTipoEquipo() == null) {
            //throw new RuntimeException("El tipo es obligatorio");
            throw new IllegalArgumentException("mensaje");
        }

        // RN-01: numeroSerie único
        if (equipoRepository.existsByNumeroSerie(equipo.getNumeroSerie())) {
            throw new RuntimeException("El número de serie ya existe");
        }

        return equipoRepository.save(equipo);
    }

    public Equipo actualizarEquipo(Long id, Equipo equipoActualizado, String username) {

        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // RN-01: validar duplicado en update
        if (equipoRepository.existsByNumeroSerieAndIdNot(
                equipoActualizado.getNumeroSerie(), id)) {
            throw new RuntimeException("El número de serie ya existe");
        }

        // RN-03: transición de estado
        validarTransicionEstado(equipo.getEstadoEquipo(), equipoActualizado.getEstadoEquipo());

        // RN-04: vida útil
        validarVidaUtil(equipoActualizado);


        // ACTUALIZACIÓN CON HISTORIAL
        // Comparamos el nombre actual de la BD contra el que viene del cliente
        if (!equipo.getNombre().equals(equipoActualizado.getNombre())) {
            guardarHistorial(equipo, "nombre", equipo.getNombre(), equipoActualizado.getNombre(), username);
            equipo.setNombre(equipoActualizado.getNombre());
        }


        // actualizar campos
        equipo.setNombre(equipoActualizado.getNombre());
        equipo.setNumeroSerie(equipoActualizado.getNumeroSerie());
        equipo.setMarca(equipoActualizado.getMarca());
        equipo.setModelo(equipoActualizado.getModelo());
        equipo.setEstadoEquipo(equipoActualizado.getEstadoEquipo());
        equipo.setTipoEquipo(equipoActualizado.getTipoEquipo());
        equipo.setFechaCompra(equipoActualizado.getFechaCompra());
        equipo.setVidaUtilAnios(equipoActualizado.getVidaUtilAnios());

        return equipoRepository.save(equipo);
    }
    
    private void guardarHistorial(Equipo equipo, String campo, String oldVal, String newVal, String user) {
        EquipoHistorial h = new EquipoHistorial();
        h.setEquipo(equipo);
        h.setCampoModificado(campo);
        h.setValorAnterior(oldVal);
        h.setValorNuevo(newVal);
        h.setFechaCambio(LocalDateTime.now());
        h.setUsuario(user);

        historialRepository.save(h);
    }


    private void validarTransicionEstado(EstadoEquipo actual, EstadoEquipo nuevo) {

        // Verificamos que los objetos no sean nulos para evitar NullPointerException
        if (actual == null || nuevo == null) return;

        String nombreActual = actual.getNombre();
        String nombreNuevo = nuevo.getNombre();

        // Regla: No se puede pasar de DADO_BAJA a OPERATIVO
        if (nombreActual.equals("DADO_BAJA") && nombreNuevo.equals("OPERATIVO")) {
            throw new BusinessException("Regla de Negocio: No se puede pasar de baja a operativo directamente");
        }
        
        // Regla: No se puede modificar un equipo que ya está DADO_BAJA a menos que sea para otro estado permitido
        // (Opcional: puedes agregar más validaciones aquí)
    }

    private void validarVidaUtil(Equipo equipo) {

    if (equipo.getFechaCompra() == null || equipo.getVidaUtilAnios() == null) {
        return;
    }

    LocalDate fechaVencimiento = equipo.getFechaCompra()
            .plusMonths(equipo.getVidaUtilAnios());

    boolean vencido = LocalDate.now().isAfter(fechaVencimiento);

        if (vencido &&
            equipo.getEstadoEquipo().getNombre().equals("OPERATIVO")) {

            throw new RuntimeException(
                "El equipo está vencido y no puede estar en estado OPERATIVO"
            );
        }
    }

    public Map<String, Long> reportePorEstado() {
    Map<String, Long> map = new HashMap<>();

    for (Object[] obj : equipoRepository.countByEstado()) {
        map.put((String) obj[0], (Long) obj[1]);
        }

        return map;
    }

    public void darDeBaja(Long id, String motivo, String autorizado) {
        Equipo e = equipoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("No existe"));
 
        if (motivo == null || autorizado == null) {
            throw new BusinessException("Motivo y autorización obligatorios");
        }

        e.setMotivoBaja(motivo);
        e.setAutorizadoPor(autorizado);
        e.setFechaBaja(LocalDate.now());

        // Se usa la instancia en minúsculas, se desenvuelve el Optional y se llama a setEstadoEquipo
        EstadoEquipo estadoBaja = estadoEquipoRepository.findByNombre("DADO_DE_BAJA")
                .orElseThrow(() -> new BusinessException("El estado DADO_DE_BAJA no existe en la base de datos"));

        e.setEstadoEquipo(estadoBaja); 

        equipoRepository.save(e);
    }
}

