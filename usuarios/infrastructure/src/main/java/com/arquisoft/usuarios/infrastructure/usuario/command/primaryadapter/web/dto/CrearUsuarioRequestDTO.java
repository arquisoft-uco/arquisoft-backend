package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CrearUsuarioCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearUsuarioRequestDTO(

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener formato valido")
        String email,

        @NotNull(message = "El rol es obligatorio")
        RolUsuarioDTO rol) {

    public CrearUsuarioCommand toCommand() {
        return CrearUsuarioCommand.crear(email, rol.getCodigo());
    }

    public enum RolUsuarioDTO {
        ESTUDIANTE("estudiante"),
        ASESOR("asesor"),
        ASESOR_FICHA("asesor-ficha"),
        COORDINADOR("coordinador"),
        JURADO("jurado"),
        BIBLIOTECARIO("bibliotecario"),
        REPRESENTANTE_COMITE_CURRICULUM("representante-comite"),
        ADMINISTRADOR("administrador");

        private final String codigo;

        RolUsuarioDTO(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigo() {
            return codigo;
        }
    }
}
