package com.arquisoft.notificaciones.application.notificacion.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record NotificacionEntity(
        UUID id,
        String idEvento,
        String tipo,
        String destinatario,
        String asunto,
        String destinatarioNombre,
        String cuerpo,
        String estado,
        String detalleError,
        Instant fechaCreacion,
        Instant fechaEnvio,
        int intentos,
        Instant fechaUltimoIntento) {
}
