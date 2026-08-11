package com.arquisoft.notificaciones.application.notificacion.command.primaryport.model;

import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;

/**
 * Datos de entrada para registrar y entregar una notificacion.
 *
 * <p>El asunto y el cuerpo llegan ya resueltos: quien traduce un evento de dominio en texto es el
 * consumidor, de modo que agregar un evento nuevo no obliga a tocar el caso de uso.
 *
 * @param idEvento           evento de dominio que origina la notificacion — clave de idempotencia
 * @param tipo               motivo de la notificacion
 * @param destinatarioNombre nombre visible del destinatario
 * @param destinatarioEmail  correo del destinatario
 * @param asunto             linea de asunto
 * @param cuerpo             cuerpo del mensaje
 */
public record EnviarNotificacionCommand(
        String idEvento,
        TipoNotificacion tipo,
        String destinatarioNombre,
        String destinatarioEmail,
        String asunto,
        String cuerpo) {
}
