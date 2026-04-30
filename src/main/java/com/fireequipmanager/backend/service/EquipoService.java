package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.model.EstadoEquipo;
import com.fireequipmanager.backend.repository.EquipoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;

    public EquipoService(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
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

    public Equipo actualizarEquipo(Long id, Equipo equipoActualizado) {

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



    private void validarTransicionEstado(EstadoEquipo actual, EstadoEquipo nuevo) {

        if (actual.getNombre().equals("DADO_BAJA") &&
            nuevo.getNombre().equals("OPERATIVO")) {

            throw new RuntimeException(
                "No se puede pasar de DADO DE BAJA a OPERATIVO directamente"
            );
        }
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



}

