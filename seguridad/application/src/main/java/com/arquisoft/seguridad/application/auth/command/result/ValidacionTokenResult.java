package com.arquisoft.seguridad.application.auth.command.result;

public record ValidacionTokenResult(
        boolean valido,
        String identidadId,
        String correo,
        String mensaje
) {}
