package com.arquisoft.notificaciones.application.notificacion.command.usecase;

import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;

public interface EnviarNotificacionUseCase {

    EnvioNotificacionResult ejecutar(NotificacionDomain entrada);
}
