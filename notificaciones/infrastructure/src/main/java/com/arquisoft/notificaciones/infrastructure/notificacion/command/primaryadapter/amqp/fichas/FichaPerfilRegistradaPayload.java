package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas;

import java.time.Instant;

public record FichaPerfilRegistradaPayload(
        String idEvento,
        Instant ocurridoEn,
        String fichaPerfilId,
        String tituloProyecto,
        ContactoPayload asesor) {

    public record ContactoPayload(String nombre, String email) {
    }
}
