package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import java.time.Instant;

public record EntregableProyectoGradoAprobadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String entregableProyectoGrado,
        String proyectoGrado,
        Integer version,
        String estadoEntregableNombre,
        String bucket,
        String claveObjeto,
        String nombreArchivo
) {
}
