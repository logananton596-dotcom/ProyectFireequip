package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.model.Equipo;
import com.fireequipmanager.backend.repository.EquipoRepository;
import org.springframework.stereotype.Service;

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
            equipo.setVidaUtilMeses(equipoActualizado.getVidaUtilMeses());
            equipo.setUbicacionActual(equipoActualizado.getUbicacionActual());
            equipo.setTipoEquipo(equipoActualizado.getTipoEquipo());
            equipo.setEstadoEquipo(equipoActualizado.getEstadoEquipo());
            return equipoRepository.save(equipo);
        }).orElse(null);
    }

    public void eliminar(Long id) {
        equipoRepository.deleteById(id);
    }
}