package com.fireequipmanager.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fireequipmanager.backend.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}