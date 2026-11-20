package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import java.time.Instant;
import java.util.List;

public record EntregableProyectoGradoCerradoPayload(
        String idEvento,
        Instant ocurridoEn,
        String entregableProyectoGrado,
        String proyectoGrado,
        String nombreProyecto,
        Integer version,
        String estadoEntregable,
        String estadoEntregableNombre,
        List<ContactoEstudiantePayload> estudiantes) {
}
