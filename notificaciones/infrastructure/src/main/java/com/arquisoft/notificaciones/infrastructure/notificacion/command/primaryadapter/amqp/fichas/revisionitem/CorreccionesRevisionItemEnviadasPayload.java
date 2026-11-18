package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.revisionitem;

import java.time.Instant;

public record CorreccionesRevisionItemEnviadasPayload(
        String idEvento,
        Instant ocurridoEn,
        String revisionItemId,
        String fichaPerfilId,
        String tituloProyecto,
        ContactoPayload asesor) {

    public record ContactoPayload(String nombre, String email) {
    }
}
