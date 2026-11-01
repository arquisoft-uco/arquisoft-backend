package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.estudianteartefacto;

import java.time.Instant;

public record EstudianteArtefactoRemovidoPayload(
        String idEvento,
        Instant ocurridoEn,
        String documento,
        ContactoPayload estudiante,
        ContactoPayload asesor) {

    public record ContactoPayload(String nombre, String email) {
    }
}
