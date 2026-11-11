package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.proyectogrado;

import java.time.Instant;
import java.util.List;

public record ProyectoGradoCanceladoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        String tituloProyecto,
        String motivo,
        List<ContactoPayload> estudiantes,
        List<ContactoPayload> asesores) {

    public record ContactoPayload(String nombre, String email) {
    }
}
