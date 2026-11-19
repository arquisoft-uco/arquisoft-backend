package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.observacionitem;

import java.time.Instant;
import java.util.List;

public record ObservacionItemReabiertaPayload(
        String idEvento,
        Instant ocurridoEn,
        String observacionItemId,
        String revisionItemId,
        String fichaPerfilId,
        String tituloProyecto,
        List<ContactoPayload> estudiantes) {

    public record ContactoPayload(String nombre, String email) {
    }
}
