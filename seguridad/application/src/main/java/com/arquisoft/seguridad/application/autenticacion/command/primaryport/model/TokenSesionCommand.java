package com.arquisoft.seguridad.application.autenticacion.command.primaryport.model;

public record TokenSesionCommand(
        String identificadorToken,
        long tiempoVidaRestante) {}
