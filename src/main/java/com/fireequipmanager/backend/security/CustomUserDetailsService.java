package com.fireequipmanager.backend.security;

import java.util.List;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fireequipmanager.backend.model.Usuario;
import com.fireequipmanager.backend.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    // Cambiamos @Autowired por constructor para mayor seguridad
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

      // Usamos el constructor extendido de User para incluir el estado "activo"
        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isActivo(), // Si es false, Spring lanzará DisabledException automáticamente
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                //List.of(new SimpleGrantedAuthority(usuario.getRol().getNombre()))
                List.of(new SimpleGrantedAuthority( "ROLE_" +usuario.getRol().getNombre()))
            );
    }
}