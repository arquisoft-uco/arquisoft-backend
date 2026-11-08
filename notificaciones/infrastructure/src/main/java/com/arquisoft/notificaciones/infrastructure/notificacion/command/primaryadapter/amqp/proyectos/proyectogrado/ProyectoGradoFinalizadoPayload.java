package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.proyectogrado;

import java.time.Instant;
import java.util.List;

public record ProyectoGradoFinalizadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        String tituloProyecto,
        List<ContactoPayload> estudiantes) {

    public record ContactoPayload(String nombre, String email) {
    }
}
