package com.arquisoft.seguridad.application.auth.command.primaryport.model;

public record AutenticarUsuarioCommand(
        String email,
        String contrasena
) {}
