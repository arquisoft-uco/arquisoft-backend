package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.asesorproyectogrado;

import java.time.Instant;

public record AsesorProyectoGradoAsignadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        String tituloProyecto,
        ContactoPayload asesor) {

    public record ContactoPayload(String nombre, String email) {
    }
}
