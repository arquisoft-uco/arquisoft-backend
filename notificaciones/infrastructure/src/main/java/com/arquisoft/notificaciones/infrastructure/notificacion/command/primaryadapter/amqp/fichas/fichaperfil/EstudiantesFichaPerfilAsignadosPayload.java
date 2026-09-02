package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.fichaperfil;

import java.time.Instant;
import java.util.List;

public record EstudiantesFichaPerfilAsignadosPayload(
        String idEvento,
        Instant ocurridoEn,
        String fichaPerfilId,
        String tituloProyecto,
        List<ContactoPayload> estudiantes) {

    public record ContactoPayload(String nombre, String email) {
    }
}
