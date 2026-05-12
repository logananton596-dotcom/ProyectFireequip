package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.TipoEquipo;
import com.fireequipmanager.backend.repository.TipoEquipoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TipoEquipoService {

    private final TipoEquipoRepository repository;

    public TipoEquipoService(TipoEquipoRepository repository) {
        this.repository = repository;
    }

    public List<TipoEquipo> listarTodos() {
        return repository.findAll();
    }

    public TipoEquipo crear(TipoEquipo tipo) {
        if (tipo.getNombre() == null || tipo.getNombre().isBlank()) {
            throw new BusinessException("El nombre del tipo de equipo es obligatorio");
        }
        // Asumiendo que tienes findByNombre en TipoEquipoRepository
        if (repository.existsByNombre(tipo.getNombre())) {
            throw new BusinessException("El tipo de equipo '" + tipo.getNombre() + "' ya existe");
        }
        return repository.save(tipo);
    }
    public TipoEquipo buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre)
                .orElseThrow(() -> new BusinessException("Tipo de equipo no encontrado: " + nombre));
    }

}