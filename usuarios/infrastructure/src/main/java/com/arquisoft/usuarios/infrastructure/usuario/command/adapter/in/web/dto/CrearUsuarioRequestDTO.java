package com.arquisoft.usuarios.infrastructure.usuario.command.adapter.in.web.dto;

import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import com.arquisoft.usuarios.application.usuario.command.model.CrearUsuarioCommand;
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
        return new CrearUsuarioCommand(email, rol.toDomain());
    }

    public enum RolUsuarioDTO {
        ESTUDIANTE, ASESOR, ASESOR_FICHA, COORDINADOR,
        JURADO, BIBLIOTECARIO, REPRESENTANTE_COMITE_CURRICULUM, ADMINISTRADOR;

        public UsuarioRole toDomain() {
            return switch (this) {
                case ESTUDIANTE -> UsuarioRole.ESTUDIANTE;
                case ASESOR -> UsuarioRole.ASESOR;
                case ASESOR_FICHA -> UsuarioRole.ASESOR_FICHA;
                case COORDINADOR -> UsuarioRole.COORDINADOR;
                case JURADO -> UsuarioRole.JURADO;
                case BIBLIOTECARIO -> UsuarioRole.BIBLIOTECARIO;
                case REPRESENTANTE_COMITE_CURRICULUM -> UsuarioRole.REPRESENTANTE_COMITE_CURRICULUM;
                case ADMINISTRADOR -> UsuarioRole.ADMINISTRADOR;
            };
        }
    }
}
