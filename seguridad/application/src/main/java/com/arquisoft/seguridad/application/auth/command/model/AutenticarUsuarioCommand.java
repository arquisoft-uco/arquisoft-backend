package com.arquisoft.seguridad.application.auth.command.model;

public record AutenticarUsuarioCommand(
        String email,
        String contrasena
) {}
