package com.arquisoft.solicitudes.application.respuesta.command.secondaryport.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public record RespuestaEntity(
        UUID id,
        UUID solicitud,
        LocalDateTime fechaRespuesta,
        String contenido,
        String estadoRespuesta) {
}
