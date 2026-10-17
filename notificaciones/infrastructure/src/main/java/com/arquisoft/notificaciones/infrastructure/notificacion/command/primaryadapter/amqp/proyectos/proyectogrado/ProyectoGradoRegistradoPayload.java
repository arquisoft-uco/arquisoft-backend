package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.proyectogrado;

import java.time.Instant;

public record ProyectoGradoRegistradoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        String fichaPerfilId,
        String tituloProyecto,
        ContactoPayload coordinador) {

    public record ContactoPayload(String nombre, String email) {
    }
}
