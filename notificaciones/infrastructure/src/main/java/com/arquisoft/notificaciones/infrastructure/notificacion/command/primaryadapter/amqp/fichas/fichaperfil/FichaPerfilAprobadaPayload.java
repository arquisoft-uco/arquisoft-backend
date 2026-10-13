package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.fichaperfil;

import java.time.Instant;
import java.util.List;

public record FichaPerfilAprobadaPayload(
        String idEvento,
        Instant ocurridoEn,
        String fichaPerfilId,
        String tituloProyecto,
        String estadoFicha,
        ContactoPayload asesor,
        List<IntegrantePayload> estudiantes) {

    public record ContactoPayload(String nombre, String email) {
    }

    public record IntegrantePayload(String estudiante, ContactoPayload contacto) {
    }
}
