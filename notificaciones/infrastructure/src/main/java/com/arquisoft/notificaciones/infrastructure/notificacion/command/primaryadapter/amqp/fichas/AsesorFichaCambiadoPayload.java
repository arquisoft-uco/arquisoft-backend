package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas;

import java.time.Instant;

public record AsesorFichaCambiadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String fichaPerfilId,
        String tituloProyecto,
        String asesorNombre,
        String asesorEmail) {
}
