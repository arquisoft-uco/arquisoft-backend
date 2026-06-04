package com.arquisoft.seguridad.application.auth.command.model;

public record TokenSesionCommand(
        String identificadorToken,
        long tiempoVidaRestante) {}
