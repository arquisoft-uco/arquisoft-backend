package com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public record SolicitudEntity(
        UUID id,
        UUID destinatario,
        UUID remitente,
        LocalDateTime fechaCreacion,
        String mensajeSolicitud,
        String tipoSolicitud) {
}
