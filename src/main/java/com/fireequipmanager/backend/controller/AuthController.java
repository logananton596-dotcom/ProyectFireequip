package com.fireequipmanager.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fireequipmanager.backend.dto.LoginResponse;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Usuario;
import com.fireequipmanager.backend.service.UsuarioService;
import com.fireequipmanager.backend.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioService usuarioService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Usuario loginRequest) {
        // 1. Buscar usuario a través del servicio
        Usuario usuario = usuarioService.buscarPorUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // 2. Verificar contraseña
        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            throw new BusinessException("Credenciales incorrectas");
        }

        // 3. Generar Token
        String token = jwtUtil.generarToken(usuario.getUsername());

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody Usuario usuario, @RequestParam(required = false) String rol) {
        // Usamos el servicio que ya maneja la encriptación y asignación de roles
        return ResponseEntity.ok(usuarioService.registrarUsuario(usuario, rol));
    }
}