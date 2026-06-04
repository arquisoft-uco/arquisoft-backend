package com.arquisoft.seguridad.application.auth.command.model;

public record AuthenticateUserCommand(
        String email,
        String password
) {}
