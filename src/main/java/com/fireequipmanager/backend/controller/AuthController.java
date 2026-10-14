package com.fireequipmanager.backend.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;


import com.fireequipmanager.backend.dto.LoginRequest;
import com.fireequipmanager.backend.dto.LoginResponse;
import com.fireequipmanager.backend.exception.BusinessException;
import com.fireequipmanager.backend.model.Usuario;
import com.fireequipmanager.backend.service.UsuarioService;
import com.fireequipmanager.backend.security.JwtUtil;

@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
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
    //public ResponseEntity<LoginResponse> login(@RequestBody Usuario loginRequest) 
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // 1. Buscar usuario a través del servicio
        Usuario usuario = usuarioService.buscarPorUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        //  AGREGA ESTAS LÍNEAS DE AUDITORÍA:
        System.out.println("--- AUDITORÍA DE LOGIN ---");
        System.out.println("Texto plano enviado desde Angular: [" + request.getPassword() + "]");
        System.out.println("Hash recuperado de la Base de Datos: [" + usuario.getPassword() + "]");
        System.out.println("¿Coinciden los valores?: " + passwordEncoder.matches(request.getPassword(), usuario.getPassword()));
        System.out.println("---------------------------");
        
        // 2. Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BusinessException("Credenciales incorrectas");
        }       

        // 3. Generar Token
        String token = jwtUtil.generarToken(usuario.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody Usuario usuario ) {
        // Usamos el servicio que ya maneja la encriptación y asignación de roles
        // Extraemos el nombre del rol del objeto anidado en el JSON si existe
        String rolNombre = (usuario.getRol() != null) ? usuario.getRol().getNombre() : null;
        // 2. Enviamos el objeto usuario junto al nombre del rol extraído al servicio
        return ResponseEntity.ok(usuarioService.registrarUsuario(usuario, rolNombre));
    }

        @GetMapping("/me")
        public ResponseEntity<Usuario> getCurrentUser() {
        // 1. Extrae el username guardado de forma segura en el token JWT actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 2. Busca al usuario con sus roles cargados para el Sidebar del Front
        Usuario usuario = usuarioService.buscarPorUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado en la sesión"));

        return ResponseEntity.ok(usuario);
    }

}