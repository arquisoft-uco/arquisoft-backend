package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.solicitudes.solicitud;

import java.time.Instant;

public record SolicitudAmpliacionPlazoEnviadaPayload(
        String idEvento,
        Instant ocurridoEn,
        String solicitudId,
        String remitenteNombre,
        String mensajeSolicitud,
        String destinatarioNombre,
        String destinatarioEmail) {
}
