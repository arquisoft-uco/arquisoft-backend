package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.revisionitem;

import java.time.Instant;
import java.util.List;

public record RevisionItemAgregadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String itemId,
        String tituloProyecto,
        List<ContactoPayload> estudiantes) {

    public record ContactoPayload(String nombre, String email) {
    }
}
