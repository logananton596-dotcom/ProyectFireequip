package com.fireequipmanager.backend.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    // Se usa solo en la respuesta (al crear viaja como null)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    // WRITE_ONLY: Permite recibir la contraseña en el POST, pero nunca la expone en el GET
    @NotBlank(message = "La contraseña es obligatoria")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private boolean activo = true;

    // El cliente envía solo el ID del rol al crear/editar
    @NotNull(message = "El ID del rol es obligatorio")
    private Long rolId;

    // El servidor llena este campo solo para las respuestas (GET/POST)
    private String rolNombre;
}