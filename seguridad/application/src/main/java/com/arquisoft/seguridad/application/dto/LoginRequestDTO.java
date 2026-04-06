package com.arquisoft.seguridad.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de login.
 * Contiene las credenciales del usuario para autenticarse contra Keycloak.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es valido")
    private String email;

    @NotBlank(message = "La contrasena es requerida")
    @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
    private String password;
}
