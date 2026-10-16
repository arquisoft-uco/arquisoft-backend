package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.solicitudes.respuesta;

import java.time.Instant;

public record SolicitudCambioAsesorRespondidaPayload(
        String idEvento,
        Instant ocurridoEn,
        String solicitudId,
        String contenido,
        String remitenteNombre,
        String remitenteEmail,
        String responsableNombre) {
}
