package com.arquisoft.seguridad.application.auth.command.primaryport.model;

public record TokenSesionCommand(
        String identificadorToken,
        long tiempoVidaRestante) {}
