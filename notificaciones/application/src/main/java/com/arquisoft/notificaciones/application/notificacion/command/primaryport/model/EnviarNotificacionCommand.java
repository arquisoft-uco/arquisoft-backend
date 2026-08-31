package com.arquisoft.notificaciones.application.notificacion.command.primaryport.model;

import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.constant.NotificacionesFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;

public record EnviarNotificacionCommand(
        String idEvento,
        TipoNotificacion tipo,
        String destinatarioNombre,
        String destinatarioEmail,
        String asunto,
        String cuerpo) {

    public static EnviarNotificacionCommand crear(String idEvento,
                                                  String tipo,
                                                  String destinatarioNombre,
                                                  String destinatarioEmail,
                                                  String asunto,
                                                  String cuerpo) {
        var result = new ValidationResult();

        ValidatorTexto.noEnBlanco(idEvento,
                NotificacionesFields.Notificacion.ID_EVENTO,
                NotificacionesCodes.Notificacion.ID_EVENTO_REQUERIDO, result);
        boolean tipoPresente = ValidatorTexto.noEnBlanco(tipo,
                NotificacionesFields.Notificacion.TIPO,
                NotificacionesCodes.Notificacion.TIPO_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(destinatarioNombre,
                NotificacionesFields.Notificacion.DESTINATARIO_NOMBRE,
                NotificacionesCodes.Notificacion.DESTINATARIO_NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(destinatarioEmail,
                NotificacionesFields.Notificacion.DESTINATARIO,
                NotificacionesCodes.Notificacion.DESTINATARIO_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(asunto,
                NotificacionesFields.Notificacion.ASUNTO,
                NotificacionesCodes.Notificacion.ASUNTO_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(cuerpo,
                NotificacionesFields.Notificacion.CUERPO,
                NotificacionesCodes.Notificacion.CUERPO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new EnviarNotificacionCommand(
                UtilTexto.aplicarTrim(idEvento),
                tipoPresente ? TipoNotificacion.desde(tipo) : TipoNotificacion.VACIO,
                UtilTexto.aplicarTrim(destinatarioNombre),
                UtilTexto.aplicarTrim(destinatarioEmail),
                UtilTexto.aplicarTrim(asunto),
                UtilTexto.aplicarTrim(cuerpo));
    }
}
