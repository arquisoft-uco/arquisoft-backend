package com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;

public interface EnviarNotificacionInteractor {

    void ejecutar(EnviarNotificacionCommand entrada);
}
