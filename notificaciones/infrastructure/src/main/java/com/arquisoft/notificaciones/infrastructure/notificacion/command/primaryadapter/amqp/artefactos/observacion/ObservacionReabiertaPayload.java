package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.observacion;

import java.time.Instant;
import java.util.List;

public record ObservacionReabiertaPayload(
        String idEvento,
        Instant ocurridoEn,
        String documento,
        Integer numeroVersion,
        ContactoPayload asesor,
        List<ContactoPayload> estudiantes,
        String descripcion) {

    public record ContactoPayload(String nombre, String email) {
    }
}
