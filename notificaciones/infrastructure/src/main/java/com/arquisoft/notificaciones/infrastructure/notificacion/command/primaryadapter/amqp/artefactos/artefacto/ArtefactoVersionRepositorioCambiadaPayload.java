package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.artefacto;

import java.time.Instant;
import java.util.List;

public record ArtefactoVersionRepositorioCambiadaPayload(
        String idEvento,
        Instant ocurridoEn,
        String documento,
        int versionAnterior,
        int versionNueva,
        List<ContactoPayload> estudiantes,
        ContactoPayload asesor) {

    public record ContactoPayload(String nombre, String email) {
    }
}
