package com.arquisoft.notificaciones.application.notificacion.command.secondaryport.mapper;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.DestinatarioNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;

public final class MensajeNotificacionMapper {

    private MensajeNotificacionMapper() {}

    public static MensajeNotificacion toMensaje(NotificacionDomain notificacion) {
        return MensajeNotificacion.textoPlano(
                new DestinatarioNotificacion(
                        notificacion.getDestinatarioNombre(), notificacion.getDestinatario()),
                notificacion.getAsunto(),
                notificacion.getCuerpo(),
                notificacion.getPie());
    }
}
