package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.estadofichaperfil;

import java.time.Instant;
import java.util.List;

public record EstadoFichaPerfilAgregadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String estadoFichaPerfilId,
        String fichaPerfilId,
        String tituloProyecto,
        String estadoFicha,
        String estadoFichaNombre,
        List<ContactoPayload> estudiantes) {

    public record ContactoPayload(String nombre, String email) {
    }
}
