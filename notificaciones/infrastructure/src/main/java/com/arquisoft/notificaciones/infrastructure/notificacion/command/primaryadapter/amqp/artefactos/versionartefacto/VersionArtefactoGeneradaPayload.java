package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.versionartefacto;

import java.time.Instant;

public record VersionArtefactoGeneradaPayload(
        String idEvento,
        Instant ocurridoEn,
        String versionArtefacto,
        String artefacto,
        int numeroVersion,
        String nombreDocumento,
        ContactoPayload asesor) {

    public record ContactoPayload(String nombre, String email) {
    }
}
