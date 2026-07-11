package com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto;

import com.arquisoft.seguridad.application.auth.command.model.AuthenticateUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(

        @NotBlank(message = "El email es requerido")
        @Email(message = "El formato del email no es valido")
        String email,

        @NotBlank(message = "La contrasena es requerida")
        @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
        String password) {

    public AuthenticateUserCommand toCommand() {
        return new AuthenticateUserCommand(email, password);
    }
}
