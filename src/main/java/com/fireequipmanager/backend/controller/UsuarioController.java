package com.fireequipmanager.backend.controller;
import com.fireequipmanager.backend.dto.UsuarioDTO;
import com.fireequipmanager.backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Permite peticiones desde el frontend
public class UsuarioController {
    private final UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    // LISTAR TODOS LOS USUARIOS
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }
    // ACTIVAR O DESACTIVAR UN USUARIO (Bloqueo de cuenta)
    // Ejemplo: PUT /api/usuarios/5/estado?activo=false
    @PutMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id, 
            @RequestParam boolean activo) {
        usuarioService.cambiarEstado(id, activo);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }
    // ACTUALIZAR CONTRASEÑA DESDE EL PANEL DE ADMINISTRACIÓN
    // Se envía la contraseña de forma segura en un JSON plano {"password": "nueva_password"}
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> actualizarPassword(
            @PathVariable Long id, 
            @RequestBody Map<String, String> request) {
        
        String nuevaPassword = request.get("password");
        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        usuarioService.actualizarPassword(id, nuevaPassword);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }
}