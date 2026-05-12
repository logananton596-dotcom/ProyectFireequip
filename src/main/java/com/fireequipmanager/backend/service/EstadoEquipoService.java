package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.EstadoEquipo;
import com.fireequipmanager.backend.repository.EstadoEquipoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EstadoEquipoService {

    private final EstadoEquipoRepository repository;

    public EstadoEquipoService(EstadoEquipoRepository repository) {
        this.repository = repository;
    }

    public List<EstadoEquipo> listarTodos() {
        return repository.findAll();
    }

    public EstadoEquipo crear(EstadoEquipo estado) {
        // Validar que el nombre no esté vacío y no sea duplicado
        if (estado.getNombre() == null || estado.getNombre().isBlank()) {
            throw new BusinessException("El nombre del estado es obligatorio");
        }
        if (repository.findByNombre(estado.getNombre()).isPresent()) {
            throw new BusinessException("El estado '" + estado.getNombre() + "' ya existe");
        }
        return repository.save(estado);
    }

    public EstadoEquipo buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre)
            .orElseThrow(() -> new BusinessException("Estado no encontrado: " + nombre));
    }
}