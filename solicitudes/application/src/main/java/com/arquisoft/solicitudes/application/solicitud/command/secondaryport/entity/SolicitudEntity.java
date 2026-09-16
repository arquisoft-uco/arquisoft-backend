package com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record SolicitudEntity(
        UUID id,
        UUID destinatario,
        UUID remitente,
        Instant fechaCreacion,
        String mensajeSolicitud,
        String tipoSolicitud) {
}
