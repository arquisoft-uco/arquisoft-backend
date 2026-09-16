package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.solicitudes.respuesta;

import java.time.Instant;

public record SolicitudNovedadCoordinadorEstadoModificadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String solicitudId,
        String nuevoEstado,
        String nuevoEstadoNombre,
        String remitenteNombre,
        String remitenteEmail,
        String coordinadorNombre) {
}
