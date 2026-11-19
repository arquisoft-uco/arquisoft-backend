package com.arquisoft.evaluaciones.infrastructure.coordinadorproyectogrado.command.primaryadapter.amqp.proyectos.coordinadorproyectogrado;

import java.time.Instant;

public record ProyectoGradoRegistradoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        CoordinadorPayload coordinador
) {
    public record CoordinadorPayload(String id, String nombre, String email) {
    }
}
