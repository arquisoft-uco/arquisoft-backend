package com.arquisoft.notificaciones.application.notificacion.command.usecase;

import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.EnvioNotificacionDomain;

public interface EnviarNotificacionUseCase {

    EnvioNotificacionResult ejecutar(EnvioNotificacionDomain entrada);
}
