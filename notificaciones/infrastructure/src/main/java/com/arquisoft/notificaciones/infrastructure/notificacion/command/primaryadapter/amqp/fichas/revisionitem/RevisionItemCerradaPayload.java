package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.revisionitem;

import java.time.Instant;
import java.util.List;

public record RevisionItemCerradaPayload(
        String idEvento,
        Instant ocurridoEn,
        String revisionItemId,
        String fichaPerfilId,
        String tituloProyecto,
        List<ContactoPayload> estudiantes) {

    public record ContactoPayload(String nombre, String email) {
    }
}
