package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.estudianteartefacto;

import java.time.Instant;
import java.util.List;

public record EstudiantesArtefactoAsignadosPayload(
        String idEvento,
        Instant ocurridoEn,
        List<String> documentos,
        List<ContactoPayload> estudiantes,
        ContactoPayload asesor) {

    public record ContactoPayload(String nombre, String email) {
    }
}
