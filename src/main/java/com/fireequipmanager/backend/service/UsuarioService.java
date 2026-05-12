package com.fireequipmanager.backend.service;

import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Rol;
import com.fireequipmanager.backend.model.Usuario;
import com.fireequipmanager.backend.repository.RolRepository;
import com.fireequipmanager.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder; // Necesario para seguridad
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, 
                          RolRepository rolRepository, 
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrarUsuario(Usuario usuario, String nombreRol) {
        // 1. Validar si el username ya existe
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new BusinessException("El nombre de usuario '" + usuario.getUsername() + "' ya está en uso");
        }

        // 2. Encriptar la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        //usuario.setActivo(true);

        // 3. Asignar el Rol único (@ManyToOne)
        // Si no se envía un nombre de rol, por defecto asignamos ROLE_USER
        String buscarRol = (nombreRol != null) ? nombreRol : "ROLE_USER";
        Rol rol = rolRepository.findByNombre(buscarRol)
                .orElseThrow(() -> new BusinessException("El rol '" + buscarRol + "' no existe en la BD"));
        
        usuario.setRol(rol); // Asignación directa según tu modelo

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public void cambiarEstado(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
    }

    public void actualizarPassword(Long id, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }
}