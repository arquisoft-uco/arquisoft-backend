package com.arquisoft.notificaciones.application.notificacion.command.interactor;

import com.arquisoft.notificaciones.application.notificacion.command.model.EnviarNotificacionCommand;

public interface EnviarNotificacionInteractor {

    void ejecutar(EnviarNotificacionCommand entrada);
}
