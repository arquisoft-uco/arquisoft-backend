package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.revisionitem;

import java.time.Instant;
import java.util.List;

public record RevisionItemModificadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String itemId,
        String estadoRevisionNombre,
        String tituloProyecto,
        List<ContactoPayload> estudiantes) {

    public record ContactoPayload(String nombre, String email) {
    }
}
