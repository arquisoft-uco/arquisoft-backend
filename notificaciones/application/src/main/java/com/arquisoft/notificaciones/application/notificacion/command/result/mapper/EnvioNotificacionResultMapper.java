package com.arquisoft.notificaciones.application.notificacion.command.result.mapper;

import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;

public final class EnvioNotificacionResultMapper {

    private EnvioNotificacionResultMapper() {}

    public static EnvioNotificacionResult toResultDuplicada(String idEvento) {
        return new EnvioNotificacionResult.Duplicada(idEvento);
    }

    public static EnvioNotificacionResult toResultEnviada(NotificacionDomain notificacion) {
        return new EnvioNotificacionResult.Enviada(
                notificacion.getIdEvento(), notificacion.getDestinatario());
    }

    public static EnvioNotificacionResult toResultFallida(NotificacionDomain notificacion) {
        return new EnvioNotificacionResult.Fallida(
                notificacion.getIdEvento(),
                notificacion.getDestinatario(),
                notificacion.getDetalleError());
    }
}
