package com.arquisoft.solicitudes.application.respuesta.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record RespuestaEntity(
        UUID id,
        UUID solicitud,
        Instant fechaRespuesta,
        String contenido,
        String estadoRespuesta) {
}
