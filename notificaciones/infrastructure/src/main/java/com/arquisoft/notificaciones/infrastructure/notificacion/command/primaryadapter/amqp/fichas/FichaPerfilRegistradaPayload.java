package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas;

import java.time.Instant;
import java.util.List;

public record FichaPerfilRegistradaPayload(
        String idEvento,
        Instant ocurridoEn,
        String fichaPerfilId,
        String tituloProyecto,
        String asesorNombre,
        String asesorEmail,
        List<DestinatarioPayload> estudiantes) {

    public record DestinatarioPayload(String nombre, String email) {
    }
}
