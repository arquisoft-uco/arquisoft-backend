package com.arquisoft.notificaciones.application.notificacion.command.primaryport.model;

import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;

public record EnviarNotificacionCommand(
        String idEvento,
        TipoNotificacion tipo,
        String destinatarioNombre,
        String destinatarioEmail,
        String asunto,
        String cuerpo) {
}
