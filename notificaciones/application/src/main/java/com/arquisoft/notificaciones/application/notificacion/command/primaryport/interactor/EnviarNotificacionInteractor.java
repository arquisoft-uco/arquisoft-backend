package com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;

public interface EnviarNotificacionInteractor {

    EnvioNotificacionResult ejecutar(EnviarNotificacionCommand entrada);
}
