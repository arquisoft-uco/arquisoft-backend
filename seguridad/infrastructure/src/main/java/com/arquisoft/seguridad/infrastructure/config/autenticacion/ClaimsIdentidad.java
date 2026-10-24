package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.springframework.boot.context.properties.bind.DefaultValue;

public record ClaimsIdentidad(
        @DefaultValue("sub") String id,
        @DefaultValue("email") String correo,
        @DefaultValue("preferred_username") String nombreUsuario,
        @DefaultValue("name") String nombre,
        @DefaultValue("") String roles) {
}
