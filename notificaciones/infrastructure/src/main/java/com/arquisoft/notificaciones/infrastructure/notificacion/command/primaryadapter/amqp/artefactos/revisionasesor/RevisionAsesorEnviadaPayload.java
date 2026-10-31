package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.revisionasesor;

import java.time.Instant;
import java.util.List;

public record RevisionAsesorEnviadaPayload(
        String idEvento,
        Instant ocurridoEn,
        String documento,
        Integer numeroVersion,
        ContactoPayload asesor,
        List<ContactoPayload> estudiantes) {

    public record ContactoPayload(String nombre, String email) {
    }
}
